#!/bin/sh

./mvnw -DskipTests=true clean verify clean:clean@jfx jlink:jlink jpackage:jpackage
