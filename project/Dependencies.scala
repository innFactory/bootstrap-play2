import play.sbt.PlayImport._
import sbt._

object Dependencies {

  //Prod
  val slickPg =  "com.github.tminglei" %% "slick-pg" % "0.16.3"
  val slickPgPlayJson = "com.github.tminglei" %% "slick-pg_play-json" % "0.16.3"
  val slickJodaMapper = "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0"
  val playJson = "com.typesafe.play" %% "play-json" % "2.7.0-M1"
  val playJsonJoda = "com.typesafe.play" %% "play-json-joda" % "2.7.0-M1"
  val slick = "com.typesafe.slick" %% "slick" % "3.2.3"
  val slickCodegen = "com.typesafe.slick" %% "slick-codegen" % "3.2.3"
  val slickHikaricp = "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3"
  val HikariCP = "com.zaxxer" % "HikariCP" % "2.7.9"
  val Joda = "joda-time" % "joda-time" % "2.10"
  val flyWayCore =  "org.flywaydb" % "flyway-core" % "5.1.1"
  val postgresql = "org.postgresql" % "postgresql" % "42.2.5"
  val swaggerPlay2 = "io.swagger" %% "swagger-play2" % "1.6.0"

  // JWT
  val firebaseAdmin = "com.google.firebase" % "firebase-admin" % "6.2.0"
  val nimbusJoseJWT = "com.nimbusds" % "nimbus-jose-jwt" % "5.3"


  //Test
  val playAhcWS = "com.typesafe.play" %% "play-ahc-ws" % "2.6.15" % Test
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
    swaggerPlay2,
    firebaseAdmin,
    nimbusJoseJWT,
    scalatestPlus,
    playAhcWS
  )

}
