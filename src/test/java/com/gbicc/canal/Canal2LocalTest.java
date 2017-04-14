package com.gbicc.canal;

/**
 * Created by root on 2017/4/12.
 */
public class Canal2LocalTest {
    public static void main(String[] args) {
        String canalURL = "192.168.121.136";
        int port = 11111;
        String destination = "example";
        String filter = "test.p,test.p1";
        String databaseName = "test";
        String databaseCode = "test";
        Canal2Local c1 = new Canal2Local(canalURL, port, destination, filter, databaseName, databaseCode);
        c1.run();
    }
}
