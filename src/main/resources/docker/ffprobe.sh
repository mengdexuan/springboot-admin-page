#!/usr/bin/env bash

name=$1

ffprobe -v quiet -show_format -show_streams -print_format json "${name}"


