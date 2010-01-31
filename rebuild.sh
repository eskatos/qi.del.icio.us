#!/bin/sh
mvn -e -o clean install -DskipTests
exit $?

