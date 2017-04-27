package com.gbicc.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.Message;
import com.gbicc.util.CanalPropertiesUtils;
import com.gbicc.util.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * Created by root on 2017/4/10.
 */
public class Canal2Local implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Canal2Local.class);
    private int retries = 0;
    private String filter;
    private String canalURL;
    private  int port;
    private final  String destination;
    private final String databaseName;
    private final String databaseCode;
    private Set<String> set=new HashSet<>();

    public void run() {
        for (String s : filter.trim().split(",")) {
            set.add(s.split("\\.")[1]);
        }
        try {
            start();
        } catch (Exception e) {
            if (retries < 5) {
                e.printStackTrace();
                retries++;
                try {
                    log.error("尝试重新链接canal服务,地址为{}:{} destination={}", canalURL, port, destination);
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                run();
            } else
                log.error("重试五次失败");
        }
    }

    /**
     * 创建单链接的客户端链接所需参数
     */
    private void start() throws Exception {
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalURL, port), destination, null, null);
//        CanalConnector connector = CanalConnectors.newClusterConnector("192.168.121.130:2181", destination, null, null);
        int batchSize = 1000;
        connector.connect();
        connector.subscribe(filter);
        //回滚
        connector.rollback();
        log.info("连接canal服务,地址为{}:{},destination为:{}", canalURL, port,destination);
        while (true) {
            // 获取指定数量的数据
            Message message = connector.getWithoutAck(batchSize);
            int size = message.getEntries().size();
            //如果没有数据变动
            if (size == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //如果有数据变动
            } else {
                sendMessage(message.getEntries());
            }
        }
    }

    //发送信息
    private void sendMessage(List<Entry> entries) throws Exception {
        retries = 0;
        for (Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND)
                continue;
            CanalEntry.Header header = entry.getHeader();

            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            // eventType是mysql操作的四种类型 select,update，insert，delete
            CanalEntry.EventType eventType = rowChange.getEventType();
            //获取表名
            String tableName = header.getTableName().toLowerCase();
            if(!set.contains(tableName)){
                log.warn("当前库为:{}.所订阅的表里不包含{}表",databaseName,tableName);
                continue;
            }else{
                switch (eventType) {
                    case INSERT:
                        rowData2local(rowChange.getRowDatasList(), tableName, "INSERT", header.getExecuteTime());
                        break;
                    case UPDATE:
                        rowData2local(rowChange.getRowDatasList(), tableName, "UPDATE", header.getExecuteTime());
                        break;
                    case DELETE:
                        rowData2local(rowChange.getRowDatasList(), tableName, "DELETE", header.getExecuteTime());
                        break;
                }
            }
        }
    }

    private void rowData2local(List<CanalEntry.RowData> rowDatas, String tableName, String type, long executeTime) throws Exception {
        //如果是删除的，取删除前数据，否则取修改后的数据
        boolean isDelete = type.equals("DELETE");
        List<com.alibaba.otter.canal.protocol.CanalEntry.Column> dataList;
        StringBuilder sb = new StringBuilder();
        for (CanalEntry.RowData rowData : rowDatas) {
            List<String> list = new ArrayList<>();
            if (isDelete)
                dataList = rowData.getBeforeColumnsList();
            else
                dataList = rowData.getAfterColumnsList();

/*
            switch (type){
                case "DELETE":
                    rowData.getBeforeColumnsList().forEach(column -> list.add(column.getValue()));
                    break;
                case "UPDATE":
                    break;
                case "INSERT":
                    rowData.getAfterColumnsList().forEach(column -> list.add(column.getValue()));
                    break;
            }*/

            for (CanalEntry.Column column : dataList) {
                list.add(column.getValue());
            }
            list.add(type);
//            list.add(DateUtils.DateToString(new Date(), DateUtils.DATE_TO_STRING_DETAIAL_PATTERN));
            list.add(DateUtils.DateToString(new Date(executeTime), DateUtils.DATE_TO_STRING_DETAIAL_PATTERN));
            sb.append(StringUtils.join(list, '\001') + "\n");
        }
        //写入文件
        writeToFile(tableName, sb.toString());
    }


    /**
     * 将数据写入到本地文件
     *
     * @param tableName
     * @param msg
     * @throws IOException
     */
    public void writeToFile(String tableName, String msg) throws IOException {
        ResourceBundle bundle = CanalPropertiesUtils.bundle;
        //本地根路径
        String localPath = bundle.getString("localPath");
        String date = DateUtils.DateToString(new Date(Start.atomicLong.get()), DateUtils.DATE_TO_STRING_SHORT_PATTERN3);
        //根路径/库名
        String dirPath = localPath
                + File.separator
                + databaseName;

        File file = new File(dirPath);
        //路径不存在先创建文件夹
        if (!file.exists())
            file.mkdirs();
        //文件路径格式: 表/yy-MM-dd/tableName+yy-MM-dd
        String filePath = dirPath + File.separator + databaseCode
                + "_" + tableName + "_" + date + ".tmp";
        log.info("当前库为:{},将{}表数据文件写入到{}路径下",databaseName, tableName, filePath);
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
        bw.write(msg);
        bw.flush();
        bw.close();
    }

    public Canal2Local(String canalURL, int port, String destination, String filter, String databaseName, String databaseCode) {
        this.filter = filter;
        this.canalURL = canalURL;
        this.port = port;
        this.destination = destination;
        this.databaseName = databaseName;
        this.databaseCode = databaseCode;
    }
}
