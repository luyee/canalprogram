package com.gbicc.flume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by root on 2017/4/14.
 */
public class WriteLog {
    protected static final Logger logger = LoggerFactory.getLogger(WriteLog.class);

    public static void main(String[] args) throws Exception {
        while (true) {
            logger.info("Hello flume. ---- " + String.valueOf(new Date().getTime()));
            Thread.sleep(2000);
        }
    }
}
