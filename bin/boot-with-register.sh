#!/bin/bash

eurekaServer=$1

echo 'Install with eurekaServer' $eurekaServer

wget https://maven.aliyun.com/repository/public/com/github/liuzhengyang/hotreload-boot/1.0.8/hotreload-boot-1.0.8-jar-with-dependencies.jar
java -jar hotreload-boot-1.0.8-jar-with-dependencies.jar -eurekaServer $eurekaServer
