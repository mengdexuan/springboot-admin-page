#!/bin/bash

# 删除 TAG 全部 images
docker rmi $(docker images) -f



