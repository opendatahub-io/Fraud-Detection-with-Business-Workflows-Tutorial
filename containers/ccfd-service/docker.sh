#!/bin/bash

mvn -f ccd-model/pom.xml clean install
mvn -f ccd-fraud-kjar/pom.xml clean install -P docker
mvn -f ccd-standard-kjar/pom.xml clean install -P docker
mvn -f ccd-service/pom.xml clean install -P docker,h2