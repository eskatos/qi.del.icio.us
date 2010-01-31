#!/bin/sh
mvn -e -o gwt:run -Prun -Ddefault.disable
exit $?

