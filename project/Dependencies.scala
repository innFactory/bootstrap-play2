import play.sbt.PlayImport._
import sbt._

object Dependencies {

  //Prod
  val slickPg =  "com.github.tminglei" %% "slick-pg" % "0.19.0"
  val slickPgPlayJson = "com.github.tminglei" %% "slick-pg_play-json" % "0.19.0"
  val slickPgJts= "com.github.tminglei" %% "slick-pg_jts" % "0.19.0"
  val slickJodaMapper = "com.github.tototoshi" %% "slick-joda-mapper" % "2.4.0"
  val playJson = "com.typesafe.play" %% "play-json" % "2.8.1"
  val playJsonJoda = "com.typesafe.play" %% "play-json-joda" % "2.8.1"
  val slick = "com.typesafe.slick" %% "slick" % "3.3.2"
  val slickCodegen = "com.typesafe.slick" %% "slick-codegen" % "3.3.2"
  val slickHikaricp = "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2"
  val HikariCP = "com.zaxxer" % "HikariCP" % "2.7.9"
  val Joda = "joda-time" % "joda-time" % "2.10"
  val flyWayCore =  "org.flywaydb" % "flyway-core" % "6.2.3"
  val postgresql = "org.postgresql" % "postgresql" % "42.2.5"
  val akkaStreams =  "com.typesafe.akka" %% "akka-stream" % "2.5.30"

  val cats = "org.typelevel" %% "cats-core" % "2.2.0-M2"

  //Test
  val playAhcWS = "com.typesafe.play" %% "play-ahc-ws" % "2.6.25" % Test
  val scalatestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

  lazy val list = Seq(
    guice,
    slickPg,
    slickPgPlayJson,
    slickJodaMapper,
    playJson,
    playJsonJoda,
    slick,
    slickCodegen,
    slickHikaricp,
    HikariCP,
    Joda,
    flyWayCore,
    postgresql,
    scalatestPlus,
    playAhcWS,
    slickPgJts,
    akkaStreams,
    cats
  )

}