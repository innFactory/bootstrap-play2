val releaseVersion = "0.0.1"

resolvers += Resolver.githubPackages("innFactory")

lazy val apiDefinition = (project in file("."))
  .enablePlugins(Smithy4sCodegenPlugin)
  .settings(
    name := "api-definition",
    scalaVersion := Dependencies.scalaVersion,
    organization := "de.innfactory.bootstrap-play2",
    version := releaseVersion,
    GithubConfig.settings
  )
