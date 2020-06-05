# RunFor.sh Documentation

The runFor.sh Script will start a Docker Postgis Container with [docker-compose.yml](docker-compose.yml)

The Postgis data volume will be mounted to: 

- __local-runner/postgis-volume__ 

so that even if the container is deleted no data will be lost!

### Start

The Script will check for both prerequisites (firebase.json, influx2.properties) and exit if not found:

``` 
./local-runner/runFor.sh
```

- Name mentioned in logs:

 ``` 
 ./local-runner/runFor.sh -n Name
 ```

- Remove docker container volume mounted at __./local-runner/postigs__:

``` 
./local-runner/runFor.sh -r
```

