#!/bin/bash

JAVA="java"

if [ "$JAVA_HOME" != "" ] ; then
    JAVA="$JAVA_HOME/bin/java"
fi

$JAVA -classpath $(echo *.jar | tr ' ' ':')triana-app-4.0.0-SNAPSHOT.jar Triana $*