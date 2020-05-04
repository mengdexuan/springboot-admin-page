#!/usr/bin/env bash

#检察应用是否存在，若不存在，则重启

#单位(分钟)
sleep_time=1
dir_name=/home/tq/adminPage
jar_name=springboot-admin-page-full.jar

pid=`ps -ef | grep ${jar_name} | grep -v grep | awk '{print $2}'`


while(true)
do
	if [ -n "$pid" ];then
	echo "存在 pid 为 $pid 的应用：$jar_name"
	else
		echo "$jar_name 应用不存在，进行重启..."
		${dir_name}/bin/run.sh
	fi
    sleep ${sleep_time}
done






