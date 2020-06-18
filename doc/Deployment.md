# Deployment and Environent Documentation

### Deployment to Docker Container

#### Docker

To create a local docker Container with the [Native Packager](https://github.com/sbt/sbt-native-packager) Plugin:
 
The service needs a Postgis Database to generate the Slick Objects and Tables.
It could be started with:

```bash
docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=test -e POSTGRES_DB=test -e POSTGRES_USER=test --name bootstrapPlay2PGBuild mdillon/postgis:latest
```

```bash
docker:publishlocal
```

### EnvVars for Configuration

- DATABASE_DB = Database Endpoint (for example play)
- DATABASE_HOST = Database Host (for example localhost)
- DATABASE_PORT = Database Port
- DATABASE_USER = Database User
- DATABASE_PASSWORD = Database Password
- FIREBASE_JSON = Authentication Json from Google Firebase
- FIREBASE_FILEPATH = Path to Firebase.json (Dont use in CI)

- HOME = HomePath
- GOOGLE_PROJECT_ID
- GOOGLE_COMPUTE_ZONE
- GOOGLE_CLUSTER_NAME
- GCLOUD_SERVICE_KEY

In the Terminal those can be set by:

```bash
export ENV_VAR=Variable
```