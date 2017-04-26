package com.gbicc.canal;

import com.gbicc.util.CanalPropertiesUtils;
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
        //放置databaseName与interval，用来控制文件改名时间间隔
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        //读取用户canal配置文件
        File confFile = new File("conf/");

        ResourceBundle bundle = CanalPropertiesUtils.bundle;
        //本地根路径
        String localPath = bundle.getString("localPath");

        File[] files = confFile.listFiles();
        //遍历配额制文件
        Arrays.asList(files).stream()
                .filter(f -> !f.isDirectory())
                .forEach(file -> {
                    Properties props = new Properties();
                    try {
                        FileInputStream fs = new FileInputStream(file);
                        props.load(fs);
                        final  String canalURL = props.getProperty("canalURL");
                        final  int port = Integer.parseInt(props.getProperty("canalPort").trim());
                        final String destination = props.getProperty("destination");
                        final String filter = props.getProperty("filter").toLowerCase();
                        final String databaseName = props.getProperty("databaseName");
                        final String databaseCode = props.getProperty("databaseCode");
                        final Integer interval = Integer.parseInt(props.getProperty("interval").trim());
                        String dirPath = localPath
                                + File.separator
                                + databaseName;
                        //将databaseName与interval放入map
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
