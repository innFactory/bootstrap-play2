import sbt.Keys.{envVars, _}
import sbt.{Resolver, _}

object Common {

  def projectSettings = Seq(
    scalaVersion := "2.12.6",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq(
      "-encoding", "UTF-8", // yes, this is 2 args
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-numeric-widen"
    ),
    resolvers ++= Seq(
      "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
       Resolver.sonatypeRepo("releases"),
       Resolver.sonatypeRepo("snapshots")),
    libraryDependencies ++= Seq(
      "javax.inject" % "javax.inject" % "1",
      "joda-time" % "joda-time" % "2.9.9",
      "org.joda" % "joda-convert" % "1.9.2",
      "com.google.inject" % "guice" % "4.1.0",
      "com.typesafe.play" %% "play-json" % "2.7.0-M1",
  "com.typesafe.play" %% "play-json-joda" % "2.7.0-M1"
    ),
    scalacOptions in Test ++= Seq("-Yrangepos")
  )
}

