import play.sbt.PlayImport._
import sbt._

object Dependencies {

  val scalaVersion = "2.13.3"
  val akkaVersion  = "2.6.14"

  val akkaTyped    = "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
  val akkaHttp     = "com.typesafe.akka" %% "akka-http" % "10.2.4"
  val akka         = "com.typesafe.akka" %% "akka-actor"       % akkaVersion

  // https://github.com/akka/akka/issues/29351
  val akkaJackson  = "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion

  val akkaStreams  = "com.typesafe.akka" %% "akka-stream" % akkaVersion

  // innFactory Utils

  val scalaUtil = "de.innfactory.scala-utils" %% "scala-utils" % "1.3.2"

  //Prod
  val slickPg         = "com.github.tminglei"  %% "slick-pg"           % "0.19.6"
  val slickPgPlayJson = "com.github.tminglei"  %% "slick-pg_play-json" % "0.19.6"
  val slickPgJts      = "com.github.tminglei"  %% "slick-pg_jts"       % "0.19.6"
  val slickJodaMapper = "com.github.tototoshi" %% "slick-joda-mapper"  % "2.4.2"
  val playJson        = "com.typesafe.play"    %% "play-json"          % "2.9.2"
  val playJsonJoda    = "com.typesafe.play"    %% "play-json-joda"     % "2.9.2"
  val slick           = "com.typesafe.slick"   %% "slick"              % "3.3.3"
  val slickCodegen    = "com.typesafe.slick"   %% "slick-codegen"      % "3.3.3"
  val slickHikaricp   = "com.typesafe.slick"   %% "slick-hikaricp"     % "3.3.3"
  val hikariCP        = "com.zaxxer"            % "HikariCP"           % "4.0.3"  exclude("org.slf4j", "slf4j-api")
  val joda            = "joda-time"             % "joda-time"          % "2.10.10"
  val postgresql      = "org.postgresql"        % "postgresql"         % "42.2.19"
  val cats = "org.typelevel" %% "cats-core" % "2.6.0"

  //Test
  val playAhcWS     = "com.typesafe.play"      %% "play-ahc-ws"        % "2.8.8" % Test
  val scalatestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test

  // Dependent on the trace exporters you want to use add one or more of the following
  val opencensusStackdriver = "io.opencensus" % "opencensus-exporter-trace-stackdriver" % "0.28.3"
  val opencensusLoggging = "io.opencensus" % "opencensus-exporter-trace-logging"     % "0.28.3"
  val opencensusJaeger = "io.opencensus" % "opencensus-exporter-trace-jaeger"     % "0.28.3"

  val opencensusStatsStackdriver = "io.opencensus" % "opencensus-exporter-stats-stackdriver" % "0.28.3"

  // If you want to use opencensus-scala inside an Akka HTTP project
  val opencensusAkkaHttp  = "com.github.sebruck" %% "opencensus-scala-akka-http" % "0.7.2"

  val sl4j        = "org.slf4j"        % "slf4j-api"                        % "1.7.30" intransitive
  val sharedDeps  = "com.google.cloud" % "google-cloud-shared-dependencies" % "0.21.1"
  val logback     = "ch.qos.logback"   % "logback-classic"                  % "1.2.3"
  val logbackCore = "ch.qos.logback"   % "logback-core"                     % "1.2.3"

  lazy val list = Seq(
    scalaUtil,
    sl4j,
    sharedDeps,
    logback,
    logbackCore,
    akkaHttp,
    opencensusStackdriver,
    opencensusLoggging,
    opencensusStatsStackdriver,
    opencensusJaeger,
    opencensusAkkaHttp,
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
