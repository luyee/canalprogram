package com.gbicc.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.Message;
import com.gbicc.util.CanalPropertiesUtils;
import com.gbicc.util.DateUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by root on 2017/4/10.
 */
public class Canal2Local implements Runnable {
    private int retries = 0;
    private String filter;
    private String canalURL;
    private int port;
    private String destination;
    private String databaseName;

    public void run() {
        try {
            start();
        } catch (Exception e) {
            if (retries < 5) {
                e.printStackTrace();
                retries++;
                run();
            } else
                System.out.println("重试5次失败");
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
        while (true) {
            // 获取指定数量的数据
            Message message = connector.getWithoutAck(batchSize);
            long batchId = message.getId();
            int size = message.getEntries().size();
            //如果没有数据变动
            if (batchId == -1 || size == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //如果有数据变动
            } else {
                sendMessage(message.getEntries());
            }
            connector.ack(batchId); // 提交确认
        }
    }

    //发送信息
    private void sendMessage(List<Entry> entries) throws Exception {
        retries = 0;
        for (Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND)
                continue;
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            // eventType是mysql操作的四种类型 select,update，insert，delete
            CanalEntry.EventType eventType = rowChange.getEventType();
            CanalEntry.Header header = entry.getHeader();
            //获取表名
            String tableName = header.getTableName();
            switch (eventType) {
                case INSERT:
                    rowData2local(rowChange.getRowDatasList(), tableName, "INSERT");
                    break;
                case UPDATE:
                    rowData2local(rowChange.getRowDatasList(), tableName, "UPDATE");
                    break;
                case DELETE:
                    rowData2local(rowChange.getRowDatasList(), tableName, "DELETE");
                    break;
            }
        }
    }

    private void rowData2local(List<CanalEntry.RowData> rowDatas, String tableName, String type) throws Exception {
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

            for (CanalEntry.Column column : dataList) {
                list.add(column.getValue());
            }
            list.add(type);
            list.add(DateUtils.DateToString(new Date(), DateUtils.DATE_TO_STRING_DETAIAL_PATTERN));
            System.out.println(StringUtils.join(list, ',') + "\n");
            sb.append(StringUtils.join(list, ',') + "\n");
        }
        //写入文件
        writeToFile(tableName, sb.toString() + "\n");
    }


    /**
     * 将数据写入到本地文件
     *
     * @param tableName
     * @param msg
     * @throws IOException
     */
    public void writeToFile(String tableName, String msg) throws IOException {
        Properties props = CanalPropertiesUtils.getInstance();
        //本地根路径
        String localPath = props.getProperty("localPath");
        String date = DateUtils.DateToString(new Date(), DateUtils.DATE_TO_STRING_SHORT_PATTERN2);
        //根路径/库名/表名
        String dirPath = localPath + File.separator + databaseName
                + File.separator + tableName;
        File file = new File(dirPath);
        //路径不存在先创建文件夹
        if (!file.exists())
            file.mkdirs();
        //文件路径格式: 表/yy-MM-dd/tableName+yy-MM-dd
        String filePath = dirPath + File.separator + tableName + date;
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
        bw.write(msg);
        bw.flush();
        bw.close();
    }

    public Canal2Local(String canalURL, int port, String destination, String filter, String databaseName) {
        this.filter = filter;
        this.canalURL = canalURL;
        this.port = port;
        this.destination = destination;
        this.databaseName = databaseName;
    }
}
