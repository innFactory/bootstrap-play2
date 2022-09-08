import sbtassembly.MergeStrategy

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

lazy val root = (project in file("."))
  .settings(
    name := "Setup",
    version := "0.0.1",
    scalaVersion := "2.13.8",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.8.0",
      "com.typesafe.play" %% "play-json" % "2.9.2"
    ),
    assembly / assemblyMergeStrategy := (_ => MergeStrategy.first)
  )
