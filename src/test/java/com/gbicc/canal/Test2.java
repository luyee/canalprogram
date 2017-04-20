package com.gbicc.canal;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by root on 2017/4/12.
 */
public class Test2 {
    private static final Logger log = LoggerFactory.getLogger(Test2.class);


    @Test
    public void a5() throws IOException {
        File confFile = new File("XX.properties/config.properties");
        Properties props = new Properties();
        props.load(new FileInputStream(confFile));
        System.out.println(props.getOrDefault("filter", "aaa"));
    }


    @Test
    public void a4() throws InterruptedException {
        AtomicLong atomicLong = new AtomicLong(new Date().getTime());
        System.out.println(atomicLong.toString());
        Thread.sleep(300);
        atomicLong.set(new Date().getTime());
        System.out.println(atomicLong.toString());


    }

    @Test
    public void a3() {
        File file = new File("E://temp//test");
        Arrays.asList(file.listFiles()).stream()
                .filter(f -> !f.isDirectory() && f.getName().endsWith(".tmp"))
                .forEach(f -> {
                    String path = f.getAbsolutePath();
                    System.out.println(path);
//                    System.out.println(path.substring(0, path.length() - 3) + "txt");
//                    f.renameTo(new File(path.substring(0, path.length() - 3) + "txt"));
                });
    }


    @Test
    public void a2() {
        String name = "E:\\temp\\test\\e110_p_20170414174730.tmp";
        System.out.println(name.substring(0, name.length() - 3) + "txt");
    }

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
