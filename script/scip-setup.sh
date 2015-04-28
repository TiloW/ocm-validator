#!/bin/bash

if [ -z "$BIN_PATH" ]; then
	BIN_PATH=$HOME/bin
fi

FILENAME="scip-3.1.0.linux.x86_64.gnu.opt.spx"
mkdir -p /tmp/scip
wget -nc http://scip.zib.de/download/release/$FILENAME.zip -O /tmp/scip/scip.zip
unzip /tmp/scip/scip.zip -d /tmp
mv /tmp/$FILENAME $BIN_PATH/scip
echo $BIN_PATH
ls -l $BIN_PATH
scip -c "quit"
