val releaseVersion = "0.0.1"

lazy val apiDefinition = (project in file("."))
  .enablePlugins(Smithy4sCodegenPlugin)
  .settings(
    name := "api-definition",
    scalaVersion := Dependencies.scalaVersion,
    organization := "de.innfactory.bootstrap-play2",
    version := releaseVersion,
    GithubConfig.settings,
    libraryDependencies += "com.disneystreaming.smithy4s" % "smithy4s-protocol" % "0.16.2"
  )
