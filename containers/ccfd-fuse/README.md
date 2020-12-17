# CCD Routes

Apache Camel router for Kafka messages to KIE servers.

## Build the container image

```shell
mvn -f pom.xml clean install -P docker
```

## !!!OR!!! Deploy to openshift in the project you're currently logged on

```shell
mvn -f pom.xml clean install -P openshift
```
