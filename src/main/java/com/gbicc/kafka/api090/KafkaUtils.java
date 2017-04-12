package com.gbicc.kafka.api090;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

/**
 * Created by root on 2017/4/11.
 */
public class KafkaUtils {
    private static KafkaProducer producer;

    public synchronized static KafkaProducer getInstance() {
        if (producer == null)
            init();
        return producer;
    }

    private KafkaUtils() {
    }


    private static void init() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CanalKafkaMain.bundle.getString("bootstrap.servers"));
        props.put("acks", "all");
        props.put("retries", 1);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer(props);
    }
}
