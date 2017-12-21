#!/bin/bash

export SBT_EXTRA_PARAMS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8002,server=y,suspend=n"
export SBT_OPTS="$SBT_OPTS $JAVA_OPTS"
./sbt "devrun"
