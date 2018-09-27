import com.typesafe.config.ConfigFactory
import org.flywaydb.sbt.FlywayPlugin.autoImport._
//settings

name := """play2-bootstrap"""
version := "1.0"
scalaVersion := "2.12.6"
crossScalaVersions := Seq("2.11.12", "2.12.6")


//projects and modules
lazy val slick = (project in file("modules/slick"))
  .settings(
    libraryDependencies ++= Dependencies.list,
    envVars := Map(
      "DATABASE_DB" -> sys.env.getOrElse("DATABASE_DB", "play")
    )
  )

lazy val flyway = (project in file("modules/flyway"))
  .enablePlugins(FlywayPlugin)
  .settings(
    libraryDependencies ++= Dependencies.list,
    envVars := Map(
      "DATABASE_DB" -> sys.env.getOrElse("DATABASE_DB", "play")
    )
  )

lazy val root = (project in file("."))
  .aggregate(slick)
  .dependsOn(slick)
  .enablePlugins(PlayScala, DockerPlugin)
  .settings(libraryDependencies ++= Dependencies.list)



//macros & compile
slickGen := slickCodeGenTask.value // register manual sbt command)
lazy val slickGen = taskKey[Seq[File]]("slickGen")
lazy val slickCodeGenTask = Def.task {
  val dir = sourceManaged.value
  val cp = (dependencyClasspath in Compile).value
  val r = (runner in Compile).value
  val s = streams.value
  val outputDir = (dir / "slick").getPath
  r.run("db.codegen.CustomizedCodeGenerator", cp.files, Array(outputDir), s.log)
  val fname = outputDir + "/dbdata/Tables.scala"
  Seq(file(fname))
}

sourceGenerators in Compile += Def.task {
  val dir = sourceManaged.value
  val cp = (dependencyClasspath in Compile).value
  val r = (runner in Compile).value
  val s = streams.value
  val outputDir = (dir / "slick").getPath
  val fname = outputDir + "/dbdata/Tables.scala"
  Seq(file(fname))
}

// Testing
fork in Test := true

coverageExcludedPackages += "<empty>;Reverse.*;router.*;.*AuthService.*;models\\\\.data\\\\..*;dbdata.Tables*;common.jwt.*;common.errorHandling.*;common.jwt.JwtFilter"

// Commands
addCommandAlias("ciTest", "; clean; slick/clean; flyway/clean; coverage; slickGen; test; coverageReport")

dockerExposedPorts := Seq(9000, 9000)