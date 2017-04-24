package com.gbicc.canal;

/**
 * Created by root on 2017/4/24.
 */
public class Test111 {
    public static void main(String[] args) {
        int i=0;
        for(;;){
            try {
                Thread.sleep(2000);
                i++;
                System.out.println("empty:"+i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
