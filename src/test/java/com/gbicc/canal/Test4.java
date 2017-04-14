package com.gbicc.canal;

/**
 * Created by root on 2017/4/14.
 */
public class Test4 {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            Test5 test5 = new Test5(i);
            new Thread(test5).start();
        }
        System.out.println("aaa");
    }

    static class Test5 implements Runnable {
        private int i;

        public Test5(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println(i);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Test6 extends Thread {

    }
}
