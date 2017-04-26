package com.gbicc.old.canal;

/**
 * Created by root on 2017/4/25.
 */
public class Start {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            ClientSample.start();
        });
        t1.start();
        Thread t2 = new Thread(() -> {
            ClientSample2.start();
        });
        t2.start();
    }
}
