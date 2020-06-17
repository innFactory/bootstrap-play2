# Play2-Bootstrap

[![codecov](https://codecov.io/gh/innFactory/bootstrap-play2/branch/master/graph/badge.svg)](https://codecov.io/gh/innFactory/bootstrap-play2)  ![Run Tests](https://github.com/innFactory/bootstrap-play2/workflows/Run%20Tests/badge.svg)

![Cats Friendly Badge](https://typelevel.org/cats/img/cats-badge-tiny.png)  

Bootstrap a rest service with Play2, isolated Slick and isolated Flyway

This project is built with:
   - Play Framework 2.8.1
   - Slick 3.3.2
   - Flyway-sbt & Flyway-Core 6.2.3
   
  A PostgreSQL Database with activated postgis extensions is needed (for geolocation queries)
   
   Swagger.json is available at /v1/swagger.json
   
## Documentation

<img src="doc-assets/RequestFlow.svg" width="100%" alt="request-flow" />  

######Request Flow 
<br/>

- [1. Filter ()](./doc/FilterDoc.md)
    - [AccessLoggingFilter](./doc/FilterDoc.md#AccessLoggingFilter)
    - [RouteBlacklistFilter](./doc/FilterDoc.md#RouteBlacklistFilter)
- [2. Controller (Http Request Handling)](./doc/ControllerDoc.md)
- [3. Repository (Data handling)](./doc/RepositoryDoc.md)
- [4. DAOs (Database Access)](./doc/DaoDoc.md)
    - [BaseDAO](./doc/DaoDoc.md#BaseSlickDAO)

## Deployment and Environment

See here for [Deployment and Environment Documentation](./doc/Deployment.md)

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

And that's it!

The service locally aviable at: <http://localhost:9000>