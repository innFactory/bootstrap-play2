#!/usr/bin/env bash

PATH_TO_SETUP_JAR=$(dirname "$0")"/target/scala-2.13/Setup-assembly-0.0.1.jar"

if [ ! -f "$PATH_TO_SETUP_JAR" ]; then
    echo "$PATH_TO_SETUP_JAR does not exist."
    exit
fi
exec scala "$PATH_TO_SETUP_JAR" "$@"
