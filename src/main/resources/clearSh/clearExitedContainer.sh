#!/bin/bash

#删除所有退出的容器

docker rm $(docker ps -f "status=exited" -q)




