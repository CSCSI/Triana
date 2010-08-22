#!/bin/bash

JAVA="java"

if [ "$JAVA_HOME" != "" ] ; then
    JAVA="$JAVA_HOME/bin/java"
fi

$JAVA -Djava.util.logging.config.file=./logging.properties -jar triana-app-@version@.jar


