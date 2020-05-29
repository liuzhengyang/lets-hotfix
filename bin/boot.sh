#!/bin/bash

echo 'Install with arguments '$@

wget https://maven.aliyun.com/repository/public/com/github/liuzhengyang/hotreload-boot/1.0.10/hotreload-boot-1.0.10-jar-with-dependencies.jar
java -jar hotreload-boot-1.0.10-jar-with-dependencies.jar $@
