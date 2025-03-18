# Sapper

![JDK](docs/java-24.svg)
[![License](docs/license.svg)](LICENSE)

Yet another Sapper game.

![Big Board](docs/board_big.png)

## Board Sizes

Three standard board sizes:
* 8 x 8, 10 mines
* 16 x 16, 40 mines
* 30 x 16, 99 mines

Custom board can be from 8 x 8 up to 30 x 24 with maximum mines number equal to (w - 1) * (h - 1).

## Build

* Set ```JAVA_HOME``` to JDK 24+.
* Execute:

```shell script
./mvnw clean verify
```

Application JAR and all dependencies will be placed in ```target/jmods```.

## Run

```shell script
./mvnw exec:exec@run
```

## Custom Run-Time Image

```shell script
./mvnw jlink:jlink
```

Run-time image will be found in ```target/jlink``` directory.

## Binary Packages

```shell script
./mvnw jpackage:jpackage
```

Binary packages will be found in ```target/dist``` directory.

## Support

There is no support for this application.
