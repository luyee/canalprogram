package com.gbicc.canal;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by root on 2017/4/12.
 */
public class Test2 {
    private static final Logger log = LoggerFactory.getLogger(Test2.class);

    @Test
    public void a1() {
        String a = "1";
        log.error("{}", a);
    }

    @Test
    public void t1() {
        File file = new File("src/main/customer");
        for (File file1 : file.listFiles()) {
            System.out.println(file1.getName());
        }
    }

    @Test
    public void t3() {
        ResourceBundle rb = ResourceBundle.getBundle("canal");
        System.out.println(rb.getString("localPath"));
    }

    @Test
    public void t2() {
        Properties props = new Properties();
        try {
            InputStream is = new FileInputStream("canal.properties");
            props.load(is);
        } catch (IOException e) {
            System.out.println("canal.properties未设置");
            e.printStackTrace();
        }
        System.out.println(props.getProperty("localPath"));
    }
}
