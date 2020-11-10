import play.sbt.PlayImport._
import sbt._

object Dependencies {

  val scalaVersion = "2.13.3"
  val akkaVersion  = "2.6.10"
  val akkaTyped    = "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
  val akka         = "com.typesafe.akka" %% "akka-actor"       % akkaVersion
  val akkaJackson  =
    "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion // https://github.com/akka/akka/issues/29351
  val akkaStreams = "com.typesafe.akka" %% "akka-stream" % akkaVersion

  // innFactory Utils

  val scalaUtil = "de.innfactory.scala-utils" %% "scala-utils" % "1.0.92"

  //Prod
  val slickPg         = "com.github.tminglei"  %% "slick-pg"           % "0.19.3"
  val slickPgPlayJson = "com.github.tminglei"  %% "slick-pg_play-json" % "0.19.3"
  val slickPgJts      = "com.github.tminglei"  %% "slick-pg_jts"       % "0.19.3"
  val slickJodaMapper = "com.github.tototoshi" %% "slick-joda-mapper"  % "2.4.2"
  val playJson        = "com.typesafe.play"    %% "play-json"          % "2.9.1"
  val playJsonJoda    = "com.typesafe.play"    %% "play-json-joda"     % "2.9.1"
  val slick           = "com.typesafe.slick"   %% "slick"              % "3.3.3"
  val slickCodegen    = "com.typesafe.slick"   %% "slick-codegen"      % "3.3.3"
  val slickHikaricp   = "com.typesafe.slick"   %% "slick-hikaricp"     % "3.3.3"
  val hikariCP        = "com.zaxxer"            % "HikariCP"           % "3.4.5"
  val joda            = "joda-time"             % "joda-time"          % "2.10.6"
  val postgresql      = "org.postgresql"        % "postgresql"         % "42.2.17"
  val cats = "org.typelevel" %% "cats-core" % "2.2.0"

  //Test
  val playAhcWS     = "com.typesafe.play"      %% "play-ahc-ws"        % "2.8.2" % Test
  val scalatestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test

  lazy val list = Seq(
    scalaUtil,
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
    hikariCP,
    joda,
    postgresql,
    scalatestPlus,
    playAhcWS,
    slickPgJts,
    akkaStreams,
    cats
  )

}
