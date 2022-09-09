#!/usr/bin/env bash

docker run --restart=always -p 3306:3306  --name mysql -v /root/mysql/data:/var/lib/mysql -e TZ=Asia/Shanghai  -e MYSQL_ROOT_PASSWORD=123456 -d mysql:8.0
