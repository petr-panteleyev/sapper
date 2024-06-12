# Sapper Game

[![JDK](https://img.shields.io/badge/Java-JDK--22%2B-orange)](https://jdk.java.net/archive/)
[![GitHub](https://img.shields.io/github/license/petr-panteleyev/sapper)](LICENSE)

Yet another Sapper game.

![Big Board](docs/board_big.png)

## Build

* Set ```JAVA_HOME``` to JDK 22+.
* Execute:

```shell script
./mvnw clean verify
```

Application JAR and all dependencies will be placed in ```target/jmods```.

## Run

```shell script
./mvnw javafx:run
```

## Binary Packages

To build binary installers perform the following steps:
* On Microsoft Windows: install [WiX Toolset 3.x](https://github.com/wixtoolset/wix3/releases), add its binary 
directory to ```PATH``` environment variable
* Execute:

```shell script
./mvnw clean verify jpackage:jpackage
```

Installation packages will be found in ```target/dist``` directory.

## Support

There is no support for this application.
