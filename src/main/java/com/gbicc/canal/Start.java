package com.gbicc.canal;

import com.gbicc.util.CanalPropertiesUtils;
import com.gbicc.util.DateUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 2017/4/12.
 */
public class Start {
    public static volatile String CURRENT_DATE = DateUtils.DateToString(new Date(), DateUtils.DATE_TO_STRING_SHORT_PATTERN3);

    public static void main(String[] args) throws Exception {
        List<String> filePathList = new ArrayList<>();
        //读取用户canal配置文件
        File confFile = new File("conf/");

        ResourceBundle bundle = CanalPropertiesUtils.bundle;
        //本地根路径
        String localPath = bundle.getString("localPath");
//        String date = DateUtils.DateToString(new Date(), DateUtils.DATE_TO_STRING_SHORT_PATTERN2);
        String date = Start.CURRENT_DATE;
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
                        String filter = props.getProperty("filter");
                        String databaseName = props.getProperty("databaseName");
                        String databaseCode = props.getProperty("databaseCode");
                        String dirPath = localPath
                                + File.separator
                                + databaseName;
                        //将源路径添加到list中
                        filePathList.add(dirPath);
                        //关闭流
                        fs.close();
//                        filePathList.add()
                        //执行主任务
                        Canal2Local c1 = new Canal2Local(canalURL, port, destination, filter, databaseName, databaseCode);
                        new Thread(c1).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();


        int interval = Integer.parseInt(CanalPropertiesUtils.bundle.getString("interval"));
        service.scheduleAtFixedRate(new SplitFileByHour(filePathList), 1, interval, TimeUnit.MINUTES);

    }

    static class SplitFileByHour implements Runnable {
        private List<String> filePathList;


        public SplitFileByHour(List<String> filePathList) {
            this.filePathList = filePathList;
        }

        @Override
        public void run() {
            CURRENT_DATE = DateUtils.DateToString(new Date(), DateUtils.DATE_TO_STRING_SHORT_PATTERN3);
            for (String s : filePathList) {
                File file = new File(s);
                if (file.exists()) {
                    for (File f : file.listFiles()) {
                        String path = f.getAbsolutePath();
                        System.out.println(path.substring(0, path.length() - 3) + "txt");
                        f.renameTo(new File(path.substring(0, path.length() - 3) + "txt"));
                    }
                }
            }
            /*filePathList.forEach(filePath -> {
                File file = new File(filePath);
                Arrays.asList(file.listFiles()).stream()
                        .filter(f -> !f.isDirectory())
                        .forEach(f -> {
                            System.out.println(f.getAbsolutePath().split(".")[0] + ".txt");
                            f.renameTo(new File(f.getAbsolutePath().split(".")[0] + ".txt"));
                        });
            });*/
        }
    }

}
