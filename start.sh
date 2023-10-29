#!/bin/bash

docker-compose down
docker image rm mgimenezdev/switch-server:latest
docker container rm switch-server
cd switch-server
mvn clean package
docker build -t mgimenezdev/switch-server:latest .
docker push mgimenezdev/switch-server:latest
cd ..
docker-compose up -d
cd switch-client
mvn clean package
java -jar target/client-0.0.1-SNAPSHOT.jar