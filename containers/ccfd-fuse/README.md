# CCD Routes

Apache Camel router for Kafka messages to KIE servers.

Build the container image with:

```shell
mvn -f pom.xml clean install -P docker
```

Deploy to openshift in the project you're currently logged on with:

```shell
mvn -f pom.xml clean install -P openshift
```
