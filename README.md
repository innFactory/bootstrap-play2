# Play2-Bootstrap

### Status

[![codecov](https://codecov.io/gh/innFactory/bootstrap-play2/branch/master/graph/badge.svg)](https://codecov.io/gh/innFactory/bootstrap-play2)  ![Run Tests](https://github.com/innFactory/bootstrap-play2/workflows/Run%20Tests/badge.svg) [![Mergify Status][mergify-status]][mergify]

[mergify]: https://mergify.io
[mergify-status]: https://img.shields.io/endpoint.svg?url=https://gh.mergify.io/badges/innFactory/bootstrap-play2&style=flat


###### Scala, Akka, Play2, Slick, Flyway, Insomnia, Sangria, GraphQL, Firebase

Bootstrap a REST and/or GraphQL(Sangria) service with Play2, isolated Slick and isolated Flyway

This project is built with:
   - Play Framework 2.8.X
   - Slick 3.3.X
   - Flyway-sbt & Flyway-Core 7.1.X
   - Akka 2.6.X
   - Sangria 2.0 for GraphQL
   - Scala 2.13.X
   
  A PostgreSQL Database with activated postgis extensions is needed (for geolocation queries)
   
   **Swagger.json is available at /v1/swagger.json**
   
## Table of Contents:

- [Play2-Bootstrap](#play2-bootstrap)
- [Status](#status)
- [Getting Started](#getting-started)
    - [Quickstart Dev Guide](#see-quickstart-dev-guidedocquickstartdevguidemd)
    - [MacOS/Linux/Unix](#macoslinuxunix)
        - [Prerequisites](#prerequisites)
        - [Run Locally](#run-locally)
    - [Windows](#windows)
- [Documentation](#documentation)
- [Dependencies](#dependencies)
    - [Service Accounts](#service-accounts)
    - [Database](#database)
- [Miscellaneous](#miscellaneous)
    - [Testing](#testing)
    - [Database Migration](#database-migration)
    - [Code Generation Slick](#slick-code-generation)
    - [Running](#running)
- [Licenses](#licenses)
- [Changes](#changes)
- [Contributors](#contributors)

## Getting Started

#### IMPORTANT:

To load innFactory-scala-utils a Github-Personal-Access-Token with package:read has to be exported as GITHUB_TOKEN.
This is necessary to load the packages from Github-Package-Registry.

#### See [Quickstart Dev Guide](./doc/QuickstartDevGuide.md)

#### Insomnia:

- [Download Insomnia](https://insomnia.rest/download) | [Docs](https://support.insomnia.rest/)
- Download and import Swagger.json to Insomnia:  
<a href="https://github.com/innFactory/bootstrap-play2/blob/master/doc-assets/insomnia-workspace.json" target="_blank"><img src="https://insomnia.rest/images/run.svg" alt="Run in Insomnia"></a>

- Configure Environment in Insomnia to match with local or prod/staging services

#### MacOS/Linux/Unix: 

##### Prerequisites: 

- Install Docker
- Install sbt
- Install openJDK 11
- firebase.json (Firebase Service-Account-Access json with firebase-admin-sdk rights) in __./conf/__

##### Run locally:

If prerequisites are met, the service can be started with:

```bash
cd ./local-runner

./runFor.sh
```

- Name mentioned in logs:

 ``` 
 ./local-runner/runFor.sh -n Name
 ```

- Remove docker container volume mounted at __./local-runner/postigs__:

``` 
./local-runner/runFor.sh -r
```

Service is then locally available at: <http://localhost:9000>

[RunForScriptDocs](local-runner/runForDoc.md)

#### Windows:

- Sorry, no out of the box solution

## Dependencies:

#### Service Accounts:

##### ./conf/firebase.json

Service Account from Google Cloud for the Firebase Admin Sdk. Needs **Editor** role.

#### Databases:

- **PostgresQl** Database with Password and User set. Needs Postgis Plugin fully installed.   

## Documentation

<img src="doc/RequestFlow.svg" width="100%" alt="request-flow" />  

###### Request Flow 
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

## Miscellaneous

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

###### Before Running this you have to run: slickGen and ciTest

To run the project, start up Play:

```bash
sbt run
```

And that's it!

The service locally aviable at: <http://localhost:9000>

## Licenses:
Liceses Markdown: [Last updated (18.06.2020)](doc/licenses.md)

## Changes:
Changes Markdown: [Changes](doc/changes.md)

## Contributors:

<a href="https://github.com/jona7o"><img src="https://avatars2.githubusercontent.com/u/8403631?s=460&u=831a4265651db985e3a043ad0fec697f68130c04&v=4" title="jona7o" width="80" height="80"></a>
<a href="https://github.com/patsta32"><img src="https://avatars2.githubusercontent.com/u/12295003?s=460&u=5f79d4aac3414271cd5393c3b97f413a417925aa&v=4" title="jona7o" width="80" height="80"></a>
