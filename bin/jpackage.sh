#!/bin/sh

mvn -DskipTests=true clean verify jlink:jlink jpackage:jpackage