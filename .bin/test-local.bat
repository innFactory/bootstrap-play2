@echo off

echo Start docker container with postgres db
docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=test -e POSTGRES_DB=test -e POSTGRES_USER=test --name bootstrapPlay2PGTest postgis/postgis:12-master

echo Wait until container is started
set /A counter=0
:loop
IF %counter% gtr 9 (
  goto failed
) ELSE (
  ncat -z localhost 5432 && echo Success: connection established && goto connection_established
  timeout /t 1 /nobreak
  set /A counter=%counter% + 1
  goto loop
)

:failed
echo Failed establishing connection && exit 1
goto:eof

:connection_established
echo Running tests
sbt ciTests
echo Shutdown docker container
docker stop bootstrapPlay2PGTest
docker rm bootstrapPlay2PGTest