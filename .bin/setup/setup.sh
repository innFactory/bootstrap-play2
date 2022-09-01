#!/usr/bin/env bash

cd "$(dirname "$0")" || exit
exec scala target/scala-2.13/Setup-assembly-0.0.1.jar "$@"