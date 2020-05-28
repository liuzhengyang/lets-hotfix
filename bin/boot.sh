#!/bin/bash

echo 'Install with arguments '$@

wget https://maven.aliyun.com/repository/public/com/github/liuzhengyang/hotreload-boot/1.0.9/hotreload-boot-1.0.9-jar-with-dependencies.jar
java -jar hotreload-boot-1.0.9-jar-with-dependencies.jar $@
