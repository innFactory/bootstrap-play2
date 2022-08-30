val releaseVersion = "0.0.1"

resolvers += Resolver.githubPackages("innFactory")

lazy val apiDefinition = (project in file("."))
  .settings(
    name := "apidefinition",
    scalaVersion := Dependencies.scalaVersion,
    organization := "de.innfactory.bootstrap-play2",
    version := releaseVersion,
    scalaVersion := Dependencies.scalaVersion,
    GithubConfig.settings
  )
