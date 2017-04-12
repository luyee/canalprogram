package com.gbicc.util;

import java.io.*;
import java.util.Properties;

/**
 * Created by root on 2017/4/12.
 */
public class CanalPropertiesUtils {
    public static Properties props = new Properties();

    static {
        init();
    }

    public static Properties getInstance() {
        if (props == null)
            init();
        return props;
    }

    private static void init() {
        try {
            InputStream is = new FileInputStream(new File("conf/canal.properties"));
            props.load(is);
        } catch (IOException e) {
            System.out.println("canal.properties未设置");
            e.printStackTrace();
        }
    }
}
