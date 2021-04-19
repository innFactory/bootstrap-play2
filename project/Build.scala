import sbt.Keys.{ envVars, _ }
import sbt.{ Resolver, _ }

object Common {

  def projectSettings =
    Seq(
      scalaVersion := Dependencies.scalaVersion, //todo fix several version statements in sbt files
      // javacOptions ++= Seq("-source", "11", "-target", "11"),
      scalacOptions ++= Seq(
        "-encoding",
        "UTF-8", // yes, this is 2 args
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
        Resolver.sonatypeRepo("snapshots")
      ),
      libraryDependencies ++= Seq(
        "javax.inject"      % "javax.inject" % "1",
        "joda-time"         % "joda-time"    % "2.9.9",
        "org.joda"          % "joda-convert" % "1.9.2",
        "com.google.inject" % "guice"        % "5.0.1"
      ),
      Test / scalacOptions ++= Seq("-Yrangepos")
    )
}
