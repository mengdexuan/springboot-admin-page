#!/usr/bin/env bash

source var.sh

docker stop ${docker_name}
docker rm ${docker_name}

