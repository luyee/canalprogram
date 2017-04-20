#!/bin/bash
ps -ef |grep -v "grep" | grep /canal/flume/conf | awk '{print $2}'  >/canal/runjar/flumebin/start.pid
cat /canal/runjar/flumebin/start.pid | while read line
do
    kill -9 $line
    echo $line
done
rm -rf /canal/runjar/flumebin/start.pid
