package com.gbicc.kafka.api090;

/**
 * Created by root on 2017/4/11.
 */
public class CanalKafkaTest {
    public static void main(String[] args) {
        String canalURL = "192.168.121.136";
        int port = 11111;
        String destination = "example";
        String filter = "test.p,test.p1";
        String topic = "temp";
        CanalKafka c1 = new CanalKafka(canalURL, port, destination, filter, topic);
        c1.run();
    }
}
