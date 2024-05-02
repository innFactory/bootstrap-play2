import sbtassembly.MergeStrategy

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

lazy val root = (project in file("."))
  .settings(
    name := "Setup",
    version := "0.0.1",
    scalaVersion := "2.13.14",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.8.0",
      "org.playframework" %% "play-json" % "2.9.2",
      "org.rogach" %% "scallop" % "4.1.0"
    ),
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x                   =>
        // For all the other files, use the default sbt-assembly merge strategy
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  )
