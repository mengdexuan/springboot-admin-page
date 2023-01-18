#!/usr/bin/env bash

docker run --restart=always -p 6379:6379 --name redis -v /root/redis/data:/data -d redis:6.2.4 redis-server --appendonly yes --requirepass redis123456


