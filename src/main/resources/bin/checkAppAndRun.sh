#!/usr/bin/env bash
jar_name=

#检察应用是否存在，若不存在，则重启

for jar_name in `ls ../|egrep '*.jar|*.war'`
do
   if [ -n "$jar_name" ];then
      echo "find  $jar_name"
   fi
   break

done

if [ "$jar_name" = "" ];then
   echo "not find *.jar or *.war"
   exit 11
fi

	
pid=`ps -ef | grep ${jar_name} | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ];then
	echo "存在 pid 为 $pid 的应用：$jar_name"
else
	echo "$jar_name 应用不存在，进行重启..."
	./run.sh
fi




