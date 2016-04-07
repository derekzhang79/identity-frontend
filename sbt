#!/bin/bash

SBT_BOOT_DIR=$HOME/.sbt/boot/

if [ ! -d "$SBT_BOOT_DIR" ]; then
  mkdir -p $SBT_BOOT_DIR
fi

echo ''
echo "********************************* JAVA VERSION *********************************"
java -version
echo "********************************************************************************"
echo ''

java -Xmx1768M -XX:+UseCompressedOops -XX:MaxMetaspaceSize=1G  \
  -Dsbt.boot.directory=$SBT_BOOT_DIR \
	$SBT_EXTRA_PARAMS \
	-jar `dirname $0`/sbt-launch.jar "$@"
