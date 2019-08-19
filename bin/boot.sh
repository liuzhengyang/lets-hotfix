#!/bin/bash

eurekaServer=$1
hostname=$(hostname -f)
[ $# -eq 0 ] && { echo "Usage: $0 <eureka-server>"; \
echo "Example: $0 localhost:8761 "; exit 1; }

REPOSRC=https://github.com/liuzhengyang/lets-hotfix
LOCALREPO=lets-hotfix

# We do it this way so that we can abstract if from just git later on
LOCALREPO_VC_DIR=$LOCALREPO/.git

if [ ! -d $LOCALREPO_VC_DIR ]
then
    git clone $REPOSRC $LOCALREPO
    cd $LOCALREPO
else
    cd $LOCALREPO
    git pull
fi

hotfixHome=`pwd`
echo $hotfixHome
./mvnw clean package -pl agent

agentPath=$hotfixHome/agent/target/agent-1.0-SNAPSHOT-jar-with-dependencies.jar

./mvnw spring-boot:run -Dserver.port=18086 -Dagent\
.path=$agentPath -Deureka.client.service-url.defaultZone=http://$eurekaServer/eureka/ -pl web