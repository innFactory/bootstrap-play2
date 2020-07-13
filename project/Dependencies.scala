import play.sbt.PlayImport._
import sbt._

object Dependencies {

  val AkkaVersion = "2.6.7"
  val akkaTyped = "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
  val akka = "com.typesafe.akka" %% "akka-actor" % AkkaVersion
  val akkaJackson = "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion // https://github.com/akka/akka/issues/29351

  //Prod
  val slickPg =  "com.github.tminglei" %% "slick-pg" % "0.19.0"
  val slickPgPlayJson = "com.github.tminglei" %% "slick-pg_play-json" % "0.19.0"
  val slickPgJts= "com.github.tminglei" %% "slick-pg_jts" % "0.19.0"
  val slickJodaMapper = "com.github.tototoshi" %% "slick-joda-mapper" % "2.4.2"
  val playJson = "com.typesafe.play" %% "play-json" % "2.9.0"
  val playJsonJoda = "com.typesafe.play" %% "play-json-joda" % "2.9.0"
  val slick = "com.typesafe.slick" %% "slick" % "3.3.2"
  val slickCodegen = "com.typesafe.slick" %% "slick-codegen" % "3.3.2"
  val slickHikaricp = "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2"
  val HikariCP = "com.zaxxer" % "HikariCP" % "3.4.5"
  val Joda = "joda-time" % "joda-time" % "2.10.6"
  val flyWayCore =  "org.flywaydb" % "flyway-core" % "6.5.0"
  val postgresql = "org.postgresql" % "postgresql" % "42.2.14"
  val akkaStreams =  "com.typesafe.akka" %% "akka-stream" % "2.6.7"

  val cats = "org.typelevel" %% "cats-core" % "2.2.0-RC1"

  //Test
  val playAhcWS = "com.typesafe.play" %% "play-ahc-ws" % "2.8.2" % Test
  val scalatestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test

  lazy val list = Seq(
    akka,
    akkaTyped,
    akkaJackson,
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