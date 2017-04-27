#!/bin/bash

 DIR=/canal/runjar/flumebin/start.pid
  if [ -f $DIR ]; then
   echo "请先停止服务"
 else
	flume-ng agent --conf /canal/flume/conf --conf-file /canal/runjar/flumeconf/AS.properties -n AS -Dflume.root.logger=INFO,console &>/canal/runjar/logs/AS.log &
	flume-ng agent --conf /canal/flume/conf --conf-file /canal/runjar/flumeconf/CS.properties -n CS -Dflume.root.logger=INFO,console &>/canal/runjar/logs/CS.log &
	flume-ng agent --conf /canal/flume/conf --conf-file /canal/runjar/flumeconf/FK.properties -n FK -Dflume.root.logger=INFO,console &>/canal/runjar/logs/FK.log &
	flume-ng agent --conf /canal/flume/conf --conf-file /canal/runjar/flumeconf/FQZ.properties -n FQZ -Dflume.root.logger=INFO,console &>/canal/runjar/logs/FQZ.log &
	flume-ng agent --conf /canal/flume/conf --conf-file /canal/runjar/flumeconf/GW.properties -n GW -Dflume.root.logger=INFO,console &>/canal/runjar/logs/GW.log &
	flume-ng agent --conf /canal/flume/conf --conf-file /canal/runjar/flumeconf/SK.properties -n SK -Dflume.root.logger=INFO,console &>/canal/runjar/logs/SK.log &
	flume-ng agent --conf /canal/flume/conf --conf-file /canal/runjar/flumeconf/XX.properties -n XX -Dflume.root.logger=INFO,console &>/canal/runjar/logs/XX.log &
	flume-ng agent --conf /canal/flume/conf --conf-file /canal/runjar/flumeconf/ZQ.properties -n ZQ -Dflume.root.logger=INFO,console &>/canal/runjar/logs/ZQ.log &
	echo "服务启动成功"
fi

