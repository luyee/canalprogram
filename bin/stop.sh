#!/bin/bash
#pid文件绝对路径
DIR=/canal/runjar/bin/start.pid
#如果pid文件存在，则读取pid文件中的进程号
if [ -f $DIR ]; then
   for PID in `cat $DIR`
   do
     echo "正在停止 $PID..."
     #杀死进程
     kill -9 $PID
   done
     #删除pid文件
     rm -f $DIR
   sleep 1
   echo "服务停止成功"
#如果pid文件不存在则报未找到服务
else
   echo "未找到服务，请确定是否启动"
fi
