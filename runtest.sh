#!/bin/bash

docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=password -e POSTGRES_DB=test -e POSTGRES_USER=user --name pgtestdata postgres:latest

(
for i in `seq 1 10`;
            do
              nc -z localhost 5432 && echo Success && exit 0
              echo -n .
              sleep 1
            done
            echo Failed waiting for Postgres && exit 1
)

sbt -DDATABASE_DB="test" ciTest
docker stop pgtestdata
docker rm pgtestdata