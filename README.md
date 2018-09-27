# Play2-Bootstrap

[![codecov.io](https://img.shields.io/codecov/c/github/innFactory/bootstrap-akka-http/master.svg?style=flat)](https://codecov.io/github/innFactory/bootstrap-akka-http)

Bootstrap a rest service with Play2, isolated Slick and isolated Flyway

This project is built with:
   - Play Framework 2.6
   - Slick 3.2.3
   - Flyway 5.1.1
   
   It needs a PostgresQL database


## Database Migration

```bash
sbt flyway/flywayMigrate
```

## Slick Code Generation

You will need to run the flywayMigrate task first, and then you will be able to generate tables using sbt-codegen.

```bash
sbt slickGen
```

## Testing

You can run functional tests against an in memory database and Slick easily with Play from a clean slate:

```bash
sbt ciTest
```

## Running

To run the project, start up Play:

```bash
sbt run
```

## Running

To create a docker Container:

```bash
sbt docker:publishLocal
```

And that's it!

Its locally aviable at: <http://localhost:9000>
