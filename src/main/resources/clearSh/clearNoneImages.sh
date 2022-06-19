#!/bin/bash

# 删除 TAG 为 <none> 的 images
docker rmi $(docker images -f 'dangling=true' -q) -f



