// Database Migrations:
// run with "sbt flywayMigrate"
// http://flywaydb.org/getstarted/firststeps/sbt.html

//$ export DB_DEFAULT_URL="jdbc:h2:/tmp/example.db"
//$ export DB_DEFAULT_USER="sa"
//$ export DB_DEFAULT_PASSWORD=""
/*
libraryDependencies += "org.flywaydb" % "flyway-core" % "5.0.3"
libraryDependencies +=  "com.typesafe.play" %% "play-json" % "2.7.0-M1"
*/

lazy val databaseHost = sys.env.getOrElse("DATABASE_HOST", "localhost")
lazy val databasePort = sys.env.getOrElse("DATABASE_PORT", "5432")
lazy val databaseDb = sys.env.getOrElse("DATABASE_DB", "play")
lazy val databaseUrl = s"jdbc:postgresql://$databaseHost:$databasePort/$databaseDb"
lazy val databaseUser = sys.env.getOrElse("DATABASE_USER", "user")
lazy val databasePassword = sys.env.getOrElse("DATABASE_PASSWORD", "password")

flywayLocations := Seq("classpath:db/migration")

flywayUrl := databaseUrl
flywayUser := databaseUser
flywayPassword := databasePassword

