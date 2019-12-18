#!/usr/bin/env bash

#mvn deploy:deploy-file\
#    -DrepositoryId=my-snapshots\
#    -Durl=http://www.cd-zq.com:8081/repository/my-snapshots/\
#    -Dfile=./target/kyb-util-1.0-SNAPSHOT.jar\
#    -DgroupId=com.zq.kyb\
#    -DartifactId=kyb-util\
#    -Dversion=1.0-SNAPSHOT
mvn clean install deploy
