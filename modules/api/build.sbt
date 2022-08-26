lazy val api = (project in file("."))
  .enablePlugins(Smithy4sCodegenPlugin)
  .settings(
    scalaVersion := Dependencies.scalaVersion,
    libraryDependencies ++= Dependencies.list,
    GithubConfig.settings,
    Compile / smithy4sInputDir := baseDirectory.value / "definition",
    Compile / smithy4sOutputDir := baseDirectory.value / "src" / "main" / "scala"
  )
