resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Flyway" at "https://davidmweber.github.io/flyway-sbt.repo"
resolvers += Resolver.url("play-sbt-plugins", url("https://dl.bintray.com/playframework/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
// Database migration
addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "6.2.3")

// Slick code generation
addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % "1.3.0")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.1")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.0.0")

addSbtPlugin("com.iheart" % "sbt-play-swagger" % "0.9.1-PLAY2.8")

addSbtPlugin("com.typesafe.sbt" % "sbt-license-report" % "1.2.0")