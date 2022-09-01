lazy val root = (project in file("."))
  .settings(
    name := "Setup",
    version := "0.0.1",
    scalaVersion := "2.13.8",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.8.0"
    )
  )
