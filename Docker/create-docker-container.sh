#!/bin/bash

mvn package
docker build -t keyz182/triana .
