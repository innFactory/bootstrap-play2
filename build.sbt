import com.typesafe.config.ConfigFactory
import sbt.{Def, _}

//settings

name := """play2-bootstrap"""
version := "1.0"
scalaVersion := "2.12.6"
crossScalaVersions := Seq("2.11.12", "2.12.6")


val generatedFilePath: String = "/dbdata/Tables.scala"
val flywayDbName: String = "bootstrapPlay2"

val dbConf = settingKey[DbConf]("Typesafe config file with slick settings")
val generateTables = taskKey[Seq[File]]("Generate slick code")

def createDbConf(dbConfFile: File): DbConf = {
  // println (s"dbConfFile: $dbConfFile")
  val configFactory = ConfigFactory.parseFile(dbConfFile)
  val configPath = s"$flywayDbName"
  val config = configFactory.getConfig(configPath).resolve
  val url =  s"${config.getString("db.urlPrefix")}${ config.getString("db.host")}:${config.getString("db.port")}/${config.getString("db.db")}"
  println(url, config)
  println(sys.env.get("DATABASE_PORT"), System.getProperty("DATABASE_PORT"))
  DbConf(
    config.getString("profile"),
    config.getString("db.driver"),
    config.getString("db.user"),
    config.getString("db.password"),
    url
  )
}

def dbConfSettings = Seq(
  dbConf in Global := createDbConf((resourceDirectory in Compile).value / "application.conf")
)

// FLYWAY

def flywaySettings = Seq(
  flywayUrl := (dbConf in Global).value.url,
  flywayUser := (dbConf in Global).value.user,
  flywayPassword := (dbConf in Global).value.password
)

lazy val flyway = (project in file("modules/flyway"))
  .enablePlugins(FlywayPlugin)
  .settings(
    libraryDependencies ++= Dependencies.list,
    flywaySettings
  )

// SLICK

lazy val slick = (project in file("modules/slick"))
  .dependsOn(flyway)
  .settings(libraryDependencies ++= Dependencies.list)

lazy val root = (project in file("."))
  .dependsOn(slick)
  .enablePlugins(PlayScala, DockerPlugin)
  .settings(
    dbConfSettings,
    libraryDependencies ++= Dependencies.list
  )

def generateTablesTask(conf: DbConf) = Def.task {
  val dir = sourceManaged.value
  val outputDir = (dir / "slick").getPath
  val fname = outputDir + generatedFilePath
  println (s"fname: $fname")
    val generator = "db.codegen.CustomizedCodeGenerator"
    val url = conf.url
    val slickProfile = conf.profile.dropRight(1)
    val jdbcDriver = conf.driver
    val pkg = "db.Tables"
    val cp = (dependencyClasspath in Compile).value
    val username = conf.user
    val password = conf.password
    val s = streams.value
    val r = (runner in Compile).value
    r.run(
        generator,
      cp.files,
        Array(outputDir, slickProfile, jdbcDriver, url, pkg, username, password),
        s.log
      )
  Seq(file(fname))
}

sourceGenerators in Compile += Def.taskDyn(generateTablesTask((dbConf in Global).value)).taskValue

// Testing

fork in Test := true

coverageExcludedPackages += "<empty>;Reverse.*;router.*;.*AuthService.*;models\\\\.data\\\\..*;dbdata.Tables*;common.jwt.*;common.errorHandling.*;common.jwt.JwtFilter"

// Commands

addCommandAlias("ciTest", "; clean; coverage; flyway/flywayMigrate; test; coverageReport")

dockerExposedPorts := Seq(9000, 9000)