val releaseVersion = "0.0.1"

resolvers += Resolver.githubPackages("innFactory")

lazy val api = (project in file("."))
  .enablePlugins(Smithy4sCodegenPlugin)
  .settings(
    name := "api",
    scalaVersion := Dependencies.scalaVersion,
    organization := "de.innfactory.bootstrap-play2",
    version := releaseVersion,
    scalaVersion := Dependencies.scalaVersion,
    libraryDependencies ++= Dependencies.list,
    GithubConfig.settings,
    Compile / smithy4sInputDir := baseDirectory.value / "src" / "main" / "scala" / "definition",
    Compile / smithy4sOutputDir := baseDirectory.value / "src" / "main" / "scala"
  )

/*
 * smithy4sOutputDir is added automatically to sbt clean
 * -> prevent source code deletion during sbt clean
 */
cleanKeepFiles += baseDirectory.value / "src" / "main" / "scala" / "definition"
