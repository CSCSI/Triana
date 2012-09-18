#!/bin/bash

bzip2 -d disttar.tar.bz2
tar -xf disttar.tar
chmod +x triana.sh
./triana.sh $*
