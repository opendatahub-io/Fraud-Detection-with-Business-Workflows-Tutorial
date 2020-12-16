#!/bin/bash

#mvn clean package
docker build -f src/main/docker/Dockerfile.native -t odh-workshops/ccfd-business-workflow-tutorial-ccfd-notification-service:latest .