#!/usr/bin/env bash

source /home/tq/pbank/bin/var.sh

echo "start $dir_name/$jar_name ..."

#-XX:MetaspaceSize=128m （元空间默认大小）
#-XX:MaxMetaspaceSize=128m （元空间最大大小）
#-Xms1024m （堆最大大小）
#-Xmx1024m （堆默认大小）
#-Xmn256m （新生代大小）
#-Xss256k （棧最大深度大小）
#-XX:SurvivorRatio=8 （新生代分区比例 8:2）
#-XX:+UseConcMarkSweepGC （指定使用的垃圾收集器，这里使用CMS收集器）
#-XX:+PrintGCDetails （打印详细的GC日志）

nohup  ${java_dir}/bin/java -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xms1024m -Xmx1024m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar ${dir_name}/${jar_name}>/dev/null --spring.config.location=${dir_name}/config/ --logging.config=${dir_name}/config/logback-spring.xml -Djasypt.encryptor.password=G0CvDz7oJn6 2>&1 &

