#!/bin/bash

grep "<error" build/reports/checkstyle/main.xml
if [ $? == 0 ]; then
    exit 1
fi

grep "<error" build/reports/checkstyle/test.xml
if [ $? == 0 ]; then
    exit 1
fi

exit 0
