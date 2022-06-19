#!/usr/bin/env bash

source var.sh

#构建docker镜像
docker build -t ${docker_name}:v1 .

