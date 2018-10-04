#!/bin/bash

docker run -d -p 5555:5432 -e POSTGRES_PASSWORD=password -e POSTGRES_DB=build -e POSTGRES_USER=user --name pgbuilddb postgres:latest

(
for i in `seq 1 10`;
            do
              nc -z localhost 5555 && echo Success && exit 0
              echo -n .
              sleep 1
            done
            echo Failed waiting for Postgres && exit 1
)

export DATABASE_PORT="5555"
export DATABASE_DB="build"
sbt flyway/flywayMigrate
sbt docker:publishLocal
docker stop pgbuilddb
docker rm pgbuilddb