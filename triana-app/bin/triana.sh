#!/bin/bash

JAVA="java"

if [ "$JAVA_HOME" != "" ] ; then
    JAVA="$JAVA_HOME/bin/java"
fi

CP=.:triana-app-@version@.jar:restless-0.1-SNAPSHOT.jar

$JAVA -classpath $CP -Djava.util.logging.config.file=./logging.properties Triana


