package com.gbicc.canal;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by root on 2017/4/12.
 */
public class Test2 {
    @Test
    public void t1(){
        File file=new File("conf");
        for (File file1 : file.listFiles()) {
            System.out.println(file1.getName());
        }
    }
    @Test
    public void t2(){
        Properties props = new Properties();
        try {
            InputStream is = new FileInputStream(new File("conf/canal.properties"));
            props.load(is);
        } catch (IOException e) {
            System.out.println("canal.properties未设置");
            e.printStackTrace();
        }
        System.out.println(props.getProperty("localPath"));
    }
}
