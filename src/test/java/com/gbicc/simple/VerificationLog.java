package com.gbicc.simple;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by root on 2017/4/25.
 */
public class VerificationLog {
    public static void main(String[] args) throws FileNotFoundException {
        Map<String, String> map = new HashMap<>();
        //读取用户canal配置文件
        File confFile = new File("conf/");

        File[] files = confFile.listFiles();
        //遍历配额制文件
        Arrays.asList(files).stream()
                .filter(f -> !f.isDirectory())
                .forEach(file -> {
                    Properties props = new Properties();
                    try {
                        FileInputStream fs = new FileInputStream(file);
                        props.load(fs);
                        String databaseName = props.getProperty("databaseName");
                        String databaseCode = props.getProperty("databaseCode");
                        map.put(databaseName.trim(), databaseCode.trim());
                        //关闭流
                        fs.close();
                        //执行主任务
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        new BufferedReader(new FileReader("C:\\Users\\root\\Desktop\\temp\\canal2Local.log")).lines().forEach(line -> {
            String[] strs = line.split("/");
            if (strs.length == 5) {
                String databaseName = strs[3];
                String databaseCode = strs[4].split("_")[0];
                if (!map.get(databaseName.trim()).equals(databaseCode))
                    System.out.println(line);
            }
        });
    }
}
