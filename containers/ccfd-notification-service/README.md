# ccfd-notification-service project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell
./mvnw quarkus:dev
```

## Packaging and running the application

The application is packageable using `./mvnw package`.
It produces the executable `ccfd-notification-service-1.1-CCFD-runner.jar` file in `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/ccfd-notification-service-1.1-CCFD-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or you can use Docker to build the native executable using: `./mvn package -Pnative -Dquarkus.native.container-build=true`.

Using Podman (version 2): `mvn package  -Pnative -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=podman`

You can then execute your binary: `./target/ccfd-notification-service-1.1-CCFD-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide .

## Creating a container image

Depending on the mode you used to compile you image (jvm or native), you can use the files `dockerbuild-jvm.sh`or `dockerbuild-native.sh` to build your container image.
If you don't know which mode to choose, tl;dr summarary: JVM mode improves throuput and response time but uses more memory ([ref](https://quarkus.io/blog/runtime-performance/#:~:text=Quarkus%20running%20on%20the%20JVM,%25%20more%20memory%20(RSS).&text=The%20tests%20ran%20for%20up,served%20over%2033%20MILLION%20requests!)).
