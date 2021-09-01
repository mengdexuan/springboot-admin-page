#!/usr/bin/env bash

#启动docker运行jar包，并指定配置目录及音频实体文件目录
docker run -d -p 2101:2101 --name="audio" -v /home/ubuntu/audio-docker/config:/app/config -v /home/ubuntu/audio-docker/log:/app/log -v /home/ubuntu/audio-docker/data:/app/data audio:v1


