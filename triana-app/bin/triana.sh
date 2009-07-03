#!/bin/bash

JAVA="java"

if [ "$JAVA_HOME" != "" ] ; then
    JAVA="$JAVA_HOME/bin/java"
fi

CP=.:triana-app-@version@.jar

$JAVA -classpath $CP -Djava.util.logging.config.file=./logging.properties Triana


