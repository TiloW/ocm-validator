#!/bin/bash

LOGFILE=/tmp/javadoc-output.log
gradle javadoc --rerun-tasks 2>&1 > /dev/null | tee $LOGFILE

if [ -s "$LOGFILE" ]; then
    exit 1
else
    exit 0
fi
