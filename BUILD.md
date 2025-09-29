# Build

Build requires [JDK 25](https://jdk.java.net/25/).

```shell script
export JAVA_HOME=/path/to/jdk25

./mvnw clean verify
```

Application JAR and all dependencies will be placed in ```target/jmods```.

# Run

```shell
./mvnw exec:exec@run
```

# Binary Distribution

## Step 1: Custom Image

Download and unpack [JavaFX JMODs distribution](https://jdk.java.net/javafx25/).

```shell
export JAVAFX_JMODS=/path/to/javafx-jmods-{javafx-version}

./mvnw -DskipTests=true clean verify jlink:jlink
```

Custom image will be found in ```target/jlink``` directory.

## Step 2: Installation

### OS X and MS Windows

```shell
./mvnw jpackage:jpackage
```

Installer packages will be found in ```target/dist``` directory.

### Linux

```jpackage``` is currently broken on Linux such that produced application may crash with SIGSEGV.

Use the following script instead:

```shell
sudo ./bin/install.sh /opt
```

Application image will be installed into ```/opt/sapper``` together with icon and .desktop file.
