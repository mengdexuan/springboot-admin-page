#!/usr/bin/env bash

source var.sh

docker run --restart=always  -d -p ${docker_port}:${docker_port} --name="${docker_name}" -v ${app_path}/config:/app/config -v ${app_path}/log:/app/log ${docker_name}:v1

