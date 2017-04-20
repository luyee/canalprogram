#!/bin/bash
#pid文件目录
DIR=/canal/runjar/bin/start.pid
#如果pid文件存在，则报请先启动服务
if [ -f $DIR ]; then
   echo "请先停止服务"
#如果文件不存在，则执行jar包
else
    java -jar /canal/runjar/canal2Local.jar &>/canal/runjar/logs/canal2Local.log &
   proc_name="canal2Local.jar"
   #启动时，查询可执行jar包的pid进程并将进程号写到pid文件中
   proc_id=`ps -ef|grep ${proc_name}|grep -v "grep"|awk '{print $2}'`
   echo $proc_id > $DIR
   echo "服务启动成功"
fi
