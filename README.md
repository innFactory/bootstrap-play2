# Play2-Bootstrap

[![codecov](https://codecov.io/gh/innFactory/bootstrap-play2/branch/master/graph/badge.svg)](https://codecov.io/gh/innFactory/bootstrap-play2)

![Cats Friendly Badge](https://typelevel.org/cats/img/cats-badge-tiny.png)  

Bootstrap a rest service with Play2, isolated Slick and isolated Flyway

This project is built with:
   - Play Framework 2.8.1
   - Slick 3.3.2
   - Flyway-sbt & Flyway-Core 6.2.3
   
  A PostgreSQL Database with activated postgis extensions is needed (for geolocation queries)
   
   Swagger.json is available at /v1/swagger.json
   
## EnvVars for Configuration

- DATABASE_DB = Database Endpoint (for example play)
- DATABASE_HOST = Database Host (for example localhost)
- DATABASE_PORT = Database Port
- DATABASE_USER = Database User
- DATABASE_PASSWORD = Database Password
- FIREBASE_JSON = Authentication Json from Google Firebase
- FIREBASE_FILEPATH = Path to Firebase.json (Dont use in CI)

In the Terminal those can be set by:

```bash
export ENV_VAR=Variable
```

Thereafter the variable could be checked by:

```bash
echo $ENV_VAR
```

## CircleCI

EnvVars:

- HOME = HomePath
- GOOGLE_PROJECT_ID
- GOOGLE_COMPUTE_ZONE
- GOOGLE_CLUSTER_NAME
- GCLOUD_SERVICE_KEY

## Authentication

- Some requests will require a Firebase JWT Token in the Authorization Header
- The Firebase.json file has to be present and filled at ./conf/firebase.json

## Running Locally

```bash
cd ./local-runner

./runFor.sh
```

## Manually executing migrations or code-gen

### Database Migration

This has to be run first

```bash
sbt flyway/flywayMigrate
```

### Slick Code Generation

You will need to run the flywayMigrate task first, and then you will be able to generate tables using slickGen.

```bash
sbt slickGen
```

after that you will have to mark the folder target/scala-x.xx/scr_managed as "generated sources root"

## Testing

You can run functional tests against an in memory database and Slick easily with Play from a clean slate:

For local Testing:

```bash
./deployment/runtest.sh
```

#### For CI:

A Postgis Database has to be available to run:

```bash
sbt ciTests
```

## Running

######Before Running this you have to run: slickGen and ciTest

To run the project, start up Play:

```bash
sbt run
```

## Docker

To create a local docker Container with the [Native Packager](https://github.com/sbt/sbt-native-packager) Plugin:

If a database is present:

```bash
docker:publishlocal
```

If no database is available:

```bash
./buildscript.sh
```

And that's it!

Its locally aviable at: <http://localhost:9000>