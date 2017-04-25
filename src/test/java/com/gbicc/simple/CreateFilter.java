package com.gbicc.simple;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 2017/4/25.
 */
public class CreateFilter {
    public static void main(String[] args) {
        File confFile = new File("C:\\Users\\root\\Desktop\\temp2");

        File[] files = confFile.listFiles();
        //遍历配额制文件
        Arrays.asList(files).stream()
                .forEach(file -> {
                    String[] strs = file.getName().split("\\.")[0].split("_");
                    String a = strs[0];
                    String b = strs[1];
//                    System.out.println(b+"_"+);
                    List<String> list = new ArrayList<>();
                    try {
                        new BufferedReader(new FileReader(file)).lines().forEach(line -> {
                            list.add(b + "." + line);
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println(a + "=====================================================================================");
                    System.out.println(StringUtils.join(list, ","));
                });
    }

    @Test
    public void q() {
        String s = "a.b";
        System.out.println(s.split("\\.")[0]);
    }
}
