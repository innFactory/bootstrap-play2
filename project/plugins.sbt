resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Flyway" at "https://flywaydb.org/repo"

//resolvers += "Flyway" at "https://davidmweber.github.io/flyway-sbt.repo"

// Database migration
addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.2.0")

// Slick code generation
// https://github.com/tototoshi/sbt-slick-codegen
addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % "1.3.0")

libraryDependencies += "com.h2database" % "h2" % "1.4.196"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"
libraryDependencies += "com.typesafe.play" %% "play-json-joda" % "2.7.0-M1"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.19")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.9")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")