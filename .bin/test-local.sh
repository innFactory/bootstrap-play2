#!/bin/bash

RED='\033[0;31m'
ORANGE='\033[0;33m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

REMOVE=$GITHUB_TOKEN

if [ "$REMOVE" == "" ]; then
    printf "${RED}NO GITHUB TOKEN SET! ${NC} \n" && exit 1
fi


docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=test -e POSTGRES_DB=test -e POSTGRES_USER=test --name bootstrapPlay2PGTest postgis/postgis:12-master



(
for i in `seq 1 10`;
            do
              nc -z localhost 5432 && echo Success && exit 0
              echo -n .
              sleep 1
            done
            echo Failed waiting for Postgres && exit 1
)

sbt ciTests
docker stop bootstrapPlay2PGTest
docker rm bootstrapPlay2PGTest