#!/usr/bin/env bash

#调用stop.sh
./stop.sh

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

echo "start $jar_name ..."




#-XX:MetaspaceSize=128m （元空间默认大小）
#-XX:MaxMetaspaceSize=128m （元空间最大大小）
#-Xms1024m （堆最大大小）
#-Xmx1024m （堆默认大小）
#-Xmn256m （新生代大小）
#-Xss256k （棧最大深度大小）
#-XX:SurvivorRatio=8 （新生代分区比例 8:2）
#-XX:+UseConcMarkSweepGC （指定使用的垃圾收集器，这里使用CMS收集器）
#-XX:+PrintGCDetails （打印详细的GC日志）

nohup  ${JAVA_HOME}/bin/java -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xms1024m -Xmx1024m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar ${jar_name}>/dev/null --logging.config=config/logback-spring.xml  2>&1 &

sleep 8
tailf log/info.log


