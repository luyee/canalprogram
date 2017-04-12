package com.gbicc.kafka.api090;

import java.io.File;

/**
 * Created by root on 2017/4/11.
 */
public class Test1 {
    public static void main(String[] args) throws Exception {
        /*List<String> list=new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        System.out.println(list.toString());
        System.out.println(StringUtils.join(list, ","));*/
        File file = new File("E://temp//p//11");
        if (!file.exists()){
            System.out.println( file.getPath());
            file.mkdirs();
        }
    }
}
