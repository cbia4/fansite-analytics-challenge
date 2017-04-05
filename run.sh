#!/usr/bin/env bash

CLASSPATH=
PROGRAM_NAME=ProcessLog.java

echo "Compiling Fansite Analytics..."
cd src
for i in `ls` 
do
	CLASSPATH=${CLASSPATH}:${i}
done

javac -classpath ".:${CLASSPATH}" $PROGRAM_NAME

echo "Running..."
java ProcessLog
echo "Cleaning up..."
rm *.class
echo "Done."