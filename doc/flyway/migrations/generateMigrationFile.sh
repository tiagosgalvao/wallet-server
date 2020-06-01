#!/bin/bash

post="0"
buildfile="../../../build.gradle"
projectVersionId=$(fgrep "version =" $buildfile | head -1 | egrep -o "[0-9]+\.[0-9]+\.[0-9]+")
currentTime=$(date -u "+%Y%m%d%H%M")
touch V${projectVersionId}_${post}_${currentTime}.sql