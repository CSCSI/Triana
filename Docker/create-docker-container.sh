#!/bin/bash

cd ..
mvn package
cd Docker

docker build -t keyz182/triana .
