package com.gbicc.canal;

import com.gbicc.util.DateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 2017/4/13.
 */
public class Test3 {
    public static volatile String CURRENT_DATE = DateUtils.DateToString(new Date(), DateUtils.DATE_TO_STRING_SHORT_PATTERN3);

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
    }

    public static void splitFileByHour(List<String> filePathList) {
        Runnable runnable = () -> {
            CURRENT_DATE = DateUtils.DateToString(new Date(), DateUtils.DATE_TO_STRING_SHORT_PATTERN3);
            filePathList.forEach(filePath -> {
                File file = new File(filePath);
                Arrays.asList(file.listFiles()).stream()
                        .filter(f -> !f.isDirectory())
                        .forEach(f -> {
                            f.renameTo(new File(f.getAbsolutePath().split(".")[0] + ".txt"));
                        });
            });
        };
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 1, 10, TimeUnit.SECONDS);
    }
}
