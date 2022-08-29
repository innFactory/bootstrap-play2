import sbt._

object Dependencies {
  val scalaVersion = "2.13.8"

  // innFactory Utils
  val endpointBuilder = "de.innfactory" %% "smithy4play" % "0.2.2-HOTFIX-4"

  lazy val list = Seq(
    endpointBuilder
  )
}
