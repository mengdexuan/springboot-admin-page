#!/usr/bin/env bash

source=$1
size=$2
colors=$3
dest=$4

#eg: ffmpeg -i a.mp3 -filter_complex "showwavespic=s=720x200:colors=#ffffff|0xff0000|0x0000ff" -frames:v 1 a.png

ffmpeg -i "${source}" -filter_complex "showwavespic=s=${size}:colors=${colors}" -frames:v 1 "${dest}"


