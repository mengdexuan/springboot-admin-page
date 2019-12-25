#!/usr/bin/env bash
jar_name=

for jar_name in `ls .|egrep '*.jar|*.war'`
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
if [ -n "$pid" ]
then
   echo "kill $jar_name  pid: $pid"
   kill $pid
   
   sleep 30
   
   pid=`ps -ef | grep ${jar_name} | grep -v grep | awk '{print $2}'`
   if [ -n "$pid" ]
   then
	   echo "执行强制退出..."
	   echo "kill -9 $jar_name  pid: $pid"
	   kill -9 $pid
   fi
fi




