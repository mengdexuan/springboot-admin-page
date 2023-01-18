#!/usr/bin/env bash
docker run --restart=always -p 27017:27017 --name mongo \
	-v /home/ubuntu/mongo/config/mongod.conf:/data/configdb/mongod.conf \
	-v /home/ubuntu/mongo/data:/data/db \
	-v /home/ubuntu/mongo/log:/var/log/mongodb \
	-e MONGO_INITDB_ROOT_USERNAME=root \
	-e MONGO_INITDB_ROOT_PASSWORD=VE#duj7wa06yhg \
	-e TZ=Asia/Shanghai \
	-d mongo:6.0.3 \
	-f /data/configdb/mongod.conf


