#!/bin/bash

mvn clean package
docker build -f src/main/docker/Dockerfile.jvm -t odh-workshops/ccfd-business-workflow-tutorial-ccfd-notification-service:latest .