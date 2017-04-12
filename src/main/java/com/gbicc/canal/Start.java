package com.gbicc.canal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by root on 2017/4/12.
 */
public class Start {
    public static void main(String[] args) throws Exception {
        File confFile = new File("conf/customer");
        Arrays.asList(confFile.listFiles()).stream()
                .filter(f -> !f.isDirectory())
                .forEach(file -> {
                    Properties props = new Properties();
                    try {
                        props.load(new FileInputStream(file));
                        String canalURL = props.getProperty("canalURL");
                        int port = Integer.parseInt(props.getProperty("canalPort").trim());
                        String destination = props.getProperty("destination");
                        String filter = props.getProperty("filter");
                        String databaseName=props.getProperty("databaseName");
                        Canal2Local c1 = new Canal2Local(canalURL, port, destination, filter,databaseName);
                        c1.run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
