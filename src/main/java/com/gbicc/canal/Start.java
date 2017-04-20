package com.gbicc.canal;

import com.gbicc.util.CanalPropertiesUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by root on 2017/4/12.
 */
public class Start {
    public static AtomicLong atomicLong = new AtomicLong(new Date().getTime());
    private static Logger log = LoggerFactory.getLogger(Start.class);

    public static void main(String[] args) throws Exception {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        //读取用户canal配置文件
        File confFile = new File("conf/");

        ResourceBundle bundle = CanalPropertiesUtils.bundle;
        //本地根路径
        String localPath = bundle.getString("localPath");
//        String date = DateUtils.DateToString(new Date(), DateUtils.DATE_TO_STRING_SHORT_PATTERN2);
        //根路径/库名/表名

        File[] files = confFile.listFiles();
        Arrays.asList(files).stream()
                .filter(f -> !f.isDirectory())
                .forEach(file -> {
                    Properties props = new Properties();
                    try {
                        FileInputStream fs = new FileInputStream(file);
                        props.load(fs);
                        String canalURL = props.getProperty("canalURL");
                        int port = Integer.parseInt(props.getProperty("canalPort").trim());
                        String destination = props.getProperty("destination");
                        String filter = props.getOrDefault("filter", ".*\\..*").toString();
                        String databaseName = props.getProperty("databaseName");
                        String databaseCode = props.getProperty("databaseCode");
                        Integer interval = Integer.parseInt(props.getProperty("interval").trim());
                        String dirPath = localPath
                                + File.separator
                                + databaseName;
                        //将源路径添加到list中
                        map.put(dirPath, interval);

                        //关闭流
                        fs.close();
                        //执行主任务
                        Canal2Local c1 = new Canal2Local(canalURL, port, destination, filter, databaseName, databaseCode);
                        new Thread(c1).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        //初始化线程池
        ScheduledExecutorService service = Executors
                .newScheduledThreadPool(map.size());
        //根据每个库设置不同时间间隔进行文件转换操作
        map.forEach((k, v) -> {
            service.scheduleWithFixedDelay(new SplitFileByDate(k), v, v, TimeUnit.MINUTES);
        });

    }

    private static void consumer() {
        ResourceBundle bundle = ResourceBundle.getBundle("kafka");
        Properties props = new Properties();
        props.put("bootstrap.servers", bundle.getString("bootstrap.servers"));
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(bundle.getString("topics").split(",")));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
        }
    }


    static class SplitFileByDate implements Runnable {
        private String databasePath;

        public SplitFileByDate(String databasePath) {
            this.databasePath = databasePath;
        }

        @Override
        public void run() {
            atomicLong.set(new Date().getTime());
            File file = new File(databasePath);
           /* Arrays.asList(file.listFiles()).stream()
                    .filter(f -> !f.isDirectory() && f.getName().endsWith(".tmp"))
                    .forEach(f -> {
                        String path = f.getAbsolutePath();
                        System.out.println(path.substring(0, path.length() - 3) + "txt");
                        f.renameTo(new File(path.substring(0, path.length() - 3) + "txt"));
                    });*/


            if (file.exists()) {
                for (File f : file.listFiles()) {
                    if (!f.isDirectory() && f.getName().endsWith(".tmp")) {
                        String path = f.getAbsolutePath();
                        String after = path.substring(0, path.length() - 3) + "txt";
                        log.info("将文件{}转化为{}", path, after);
                        f.renameTo(new File(after));
                    }
                }
            }

        }
    }

}
