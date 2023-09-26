import play.sbt.PlayImport._
import sbt._

object Dependencies {

  val scalaVersion = "2.13.8"
  val akkaVersion = "2.6.20"
  val akkaManagementVersion = "1.1.3"

  val akkaTyped = "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.2.10"
  val akka = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaDiscovery = "com.typesafe.akka" %% "akka-discovery" % akkaVersion
  val akkaManagementClusterHttp =
    "com.lightbend.akka.management" %% "akka-management-cluster-http" % akkaManagementVersion
  val akkaManagementClusterBootstrap =
    "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % akkaManagementVersion
  val akkaDiscoveryKubernetes =
    "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % akkaManagementVersion
  val akkaClusterShardingTyped = "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion
  // https://github.com/akka/akka/issues/29351
  val akkaJackson = "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion
  val akkaStreams = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akkaSpray = "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.9"

  // innFactory Utils
  val scalaUtil = "de.innfactory.scala-utils" %% "scala-utils" % "2.0.1"
  val smithy4play = "de.innfactory" %% "smithy4play" % "0.3.1"

  // Prod
  val slickPg = "com.github.tminglei" %% "slick-pg" % "0.20.4"
  val slickPgPlayJson = "com.github.tminglei" %% "slick-pg_play-json" % "0.20.4"
  val slickPgJts = "com.github.tminglei" %% "slick-pg_jts" % "0.20.4"
  val slickJodaMapper = "com.github.tototoshi" %% "slick-joda-mapper" % "2.4.2"
  val playJson = "com.typesafe.play" %% "play-json" % "2.9.3"
  val playJsonJoda = "com.typesafe.play" %% "play-json-joda" % "2.9.3"
  val slick = "com.typesafe.slick" %% "slick" % "3.3.3"
  val slickCodegen = "com.typesafe.slick" %% "slick-codegen" % "3.3.3"
  val slickHikaricp = "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3"
  val joda = "joda-time" % "joda-time" % "2.12.0"
  val hikariCP = "com.zaxxer" % "HikariCP" % "5.0.1"
  val postgresql = "org.postgresql" % "postgresql" % "42.5.0"
  val cats = "org.typelevel" %% "cats-core" % "2.8.0"
  val chimney = "io.scalaland" %% "chimney" % "0.6.2"
  val jodaJackson = "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.11.4"

  // Test
  val playAhcWS = "com.typesafe.play" %% "play-ahc-ws" % "2.9.0-RC2" % Test
  val scalatestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test

  // opentelemetry
  val opentelemetryApi = "io.opentelemetry" % "opentelemetry-api" % "1.18.0"
  val opentelemetryBom = "io.opentelemetry" % "opentelemetry-bom" % "1.18.0"

  val sl4j = "org.slf4j" % "slf4j-api" % "2.0.3"
  val sharedDeps = "com.google.cloud" % "google-cloud-shared-dependencies" % "3.0.4"
  val logback = "ch.qos.logback" % "logback-classic" % "1.2.11"
  val logbackCore = "ch.qos.logback" % "logback-core" % "1.2.11"

  val nimbusJwt = "com.nimbusds" % "nimbus-jose-jwt" % "9.15.2"

  val firebase = "com.google.firebase" % "firebase-admin" % "8.1.0"

  val testTraits = "software.amazon.smithy" % "smithy-protocol-test-traits" % "1.24.0"

  lazy val list = Seq(
    scalaUtil,
    sl4j,
    chimney,
    sharedDeps,
    logback,
    logbackCore,
    akkaHttp,
    akka,
    akkaTyped,
    akkaJackson,
    akkaClusterShardingTyped,
    akkaManagementClusterBootstrap,
    akkaManagementClusterHttp,
    akkaDiscoveryKubernetes,
    akkaDiscovery,
    akkaSpray,
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
    cats,
    smithy4play,
    ws,
    opentelemetryApi,
    opentelemetryBom,
    nimbusJwt,
    firebase,
    jodaJackson,
    testTraits
  )

}
