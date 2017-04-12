package com.gbicc.kafka.api090;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.Message;
import com.gbicc.util.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by root on 2017/4/10.
 */
public class CanalKafka implements Runnable {
    private int retries = 0;
    private String filter;
    private String canalURL;
    private int port;
    private String topic;
    private String destination;

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
                    sendAction(rowChange.getRowDatasList(), tableName, "INSERT");
                    break;
                case UPDATE:
                    sendAction(rowChange.getRowDatasList(), tableName, "UPDATE");
                    break;
            }
        }


    }

    //发送数据到kafka
    private void sendAction(List<CanalEntry.RowData> rowDatas, String tableName, String type) throws Exception {
        Producer producer = KafkaUtils.getInstance();
        //遍历数据
        for (CanalEntry.RowData rowData : rowDatas) {
            //数据json对象
            JSONObject msg = new JSONObject();
            //表数据json对象
            JSONObject json = new JSONObject();
            List<String> list = new ArrayList<>();
            for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                json.put(column.getName(), column.getValue());
                list.add(column.getValue());
            }
            list.add(type);
            list.add(DateUtils.DateToString(new Date(), DateUtils.DATE_TO_STRING_DETAIAL_PATTERN));
            msg.put(tableName, json);
            msg.put("optype", type);
            //发送到kafka中
            producer.send(new ProducerRecord(topic, tableName, msg.toString()));
            System.out.println(msg.toString());
            //写入文件
            writeToFile(tableName, StringUtils.join(list, ',') + "\n");
        }
    }

    public void writeToFile(String tableName, String msg) throws IOException {
        String dirPath = CanalKafkaMain.bundle.getString("localPath")
                + File.separator + tableName + File.separator;
        File file = new File(dirPath);
        //路径不存在先创建文件夹
        if (!file.exists())
            file.mkdirs();
        //文件格式是当前时间yy-MM-dd
        String filePath = dirPath + DateUtils.DateToString(new Date(), DateUtils.DATE_TO_STRING_SHORT_PATTERN);
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
        bw.write(msg);
        bw.flush();
        bw.close();
    }

    public CanalKafka(String canalURL, int port, String destination, String filter, String topic) {
        this.filter = filter;
        this.canalURL = canalURL;
        this.port = port;
        this.topic = topic;
        this.destination = destination;
    }
}
