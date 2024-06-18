resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "Flyway" at "https://davidmweber.github.io/flyway-sbt.repo"
resolvers += Resolver.url("play-sbt-plugins", url("https://dl.bintray.com/playframework/sbt-plugin-releases/"))(
  Resolver.ivyStylePatterns
)

// Database migration
addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "7.4.0")

// Slick code generation
addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % "2.2.0")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.4")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.12")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.10.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
addSbtPlugin("io.github.play-swagger" % "sbt-play-swagger" % "2.0.2")

addSbtPlugin("com.github.sbt" % "sbt-license-report" % "1.6.1")

addSbtPlugin("com.codecommit" % "sbt-github-packages" % "0.5.3")

addSbtPlugin("com.disneystreaming.smithy4s" % "smithy4s-sbt-codegen" % "0.17.19")
