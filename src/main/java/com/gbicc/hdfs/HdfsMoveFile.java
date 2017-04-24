//package com.gbicc.hdfs;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileStatus;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.util.ResourceBundle;
//
///**
// * Created by root on 2017/4/21.
// */
//public class HdfsMoveFile {
//    private static final Logger log = LoggerFactory.getLogger(HdfsMoveFile.class);
//
//    public static void main(String[] args) throws Exception {
//        ResourceBundle bundle = ResourceBundle.getBundle("movefile");
//        String targetDir = bundle.getString("targetDir");
//        String sourceDir = bundle.getString("sourceDir");
//        Configuration conf = new Configuration();
//        FileSystem fs = FileSystem.get(conf);
//        FileStatus[] statuses = fs.listStatus(new Path(sourceDir));
//        for (FileStatus status : statuses) {
//            if (status.isDirectory()) {
//                FileStatus[] fileStatuses = fs.listStatus(status.getPath());
//                for (FileStatus fileStatuse : fileStatuses) {
//                    String temp = fileStatuse.getPath().getName();
//                    String abspath = targetDir + File.separator + temp.split("_")[1];
//                    Path path = new Path(abspath);
//                    if (!fs.exists(path))
//                        fs.mkdirs(path);
//                    Path targetPath = new Path(path.toString() + File.separator + temp);
//                    fs.rename(fileStatuse.getPath(), targetPath);
//                    log.info("{}  =>>>   {}", fileStatuse.getPath(), path);
//                }
//            }
//        }
//        fs.close();
//    }
//}
