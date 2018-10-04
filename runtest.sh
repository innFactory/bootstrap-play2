#!/bin/bash

docker run -d -p 5433:5432 -e POSTGRES_PASSWORD=password -e POSTGRES_DB=test -e POSTGRES_USER=user --name pgtestdata postgres:latest

(
for i in `seq 1 10`;
            do
              nc -z localhost 5433 && echo Success && exit 0
              echo -n .
              sleep 1
            done
            echo Failed waiting for Postgres && exit 1
)

#sbt -DDATABASE_DB="test" ciTest
export DATABASE_PORT="5433"
export DATABASE_DB="test"
sbt ciTest
docker stop pgtestdata
docker rm pgtestdata