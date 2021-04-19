#!/bin/bash

NAME="Development"
REMOVE=0

RED='\033[0;31m'
ORANGE='\033[0;33m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color
INFLUX=../conf/influx2.properties
FIREBASEJSON=../conf/firebase.json
PG="Postgis | Autostart"

while getopts ":rn:" opt; do
  case ${opt} in
    r )
      REMOVE=1
      ;;
    n )
      NAME=$OPTARG
      ;;
    \? ) echo "Usage: cmd [-n] name [-r]"
      ;;
  esac
done

printf "\n\n${ORANGE} - - Welcome ${NAME} - - ${NC}\n\n"

if [ $REMOVE -gt 0 ]; then
    printf "${RED}REMOVING DOCKER VOLUME!${NC}"
    docker-compose down
    rm -rf ./local-runner/postgis-volume
    printf "\n${GREEN}VOLUME REMOVED ${NC}\n\n"
fi

REMOVE=$GITHUB_TOKEN

if [ "$REMOVE" == "" ]; then
    printf "${RED}NO GITHUB TOKEN SET! ${NC} \n" && exit 1
fi

echo "Prerequisites:"

_indented_influx=$(printf '%-20s' "$INFLUX")
_indented_influx=${_indented_influx}

_indented_firebase=$(printf '%-20s' "$FIREBASEJSON")
_indented_firebase=${_indented_firebase}

_indented_pg=$(printf '%-20s' "$PG")
_indented_pg=${_indented_pg}


printf "${BLUE}(%2d) %s ${NC}\n" "1" "$_indented_firebase"
printf "${BLUE}(%2d) %s ${NC}\n" "2" "$_indented_pg"
#printf "${BLUE}(%2d) %s ${NC}\n" "3" "$_indented_influx"
printf "\n${NC}"


#if [ -f "$INFLUX" ]; then
#   printf "$INFLUX exist | ${GREEN}OK${NC}\n"
#else
#    printf "$INFLUX does not exist | ${RED}ERROR${NC}\n"
#    exit 0
#fi



if [ -f "$FIREBASEJSON" ]; then
    printf "$FIREBASEJSON exist | ${GREEN}OK${NC}\n"
else
    printf "$FIREBASEJSON does not exist | ${RED}ERROR${NC}\n"
    exit 0
fi

printf "\n ${BLUE}If you know a Database is running and you want to use it, you can ignore the Docker error${NC} \n "
printf "\n ${BLUE}Starting Database for ${NAME} ${NC} \n "
sleep 1
printf "."
sleep 1
printf "."
sleep 1
printf ".\n"

DOCKER_CONNECT=0

docker-compose up -d
printf "\n ${BLUE}"Wating for Database for ${NAME} "${NC}"

(
for i in `seq 1 20`;
            do
              nc -z localhost 5432 && printf "\n ${GREEN}SUCCESS${NC} | Connected to Postgres Container  \n" && DOCKER_CONNECT=${5} && exit 0
              echo -n .
              sleep 1
            done
            printf "\n ${RED}ERROR${NC} | connected to Postgres Container  \n" && exit 1
)

nc -z localhost 5432 ] && DOCKER_CONNECT=1

if [ $DOCKER_CONNECT -eq 0 ]; then
    exit 1
fi

export DATABASE_PORT="5432"
export DATABASE_DB="test"

printf "\n ${BLUE}Migrating Database for ${NAME} ${NC} \n "
if [ $REMOVE -gt 0 ]; then
  sleep 10
fi
sleep 5
cd ..
sbt flyway/flywayMigrate
printf "\n ${BLUE}Generating Code for ${NAME} ${NC} \n"
sbt slickGen
clear
printf "\n ${BLUE}Starting Service for ${NAME} ${NC} \n"
sbt run