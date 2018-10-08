import com.typesafe.config.ConfigFactory
import sbt.{Def, _}

//settings

name := """play2-bootstrap"""
version := "1.0"
scalaVersion := "2.12.6"

val latest = Option(System.getProperty("branch")) match {
  case Some(str) => if (str.equals("master")) true else false
  case None => false
}

val buildVersion = Option(System.getProperty("version")) match {
  case Some(str) => str
  case None => version.key.toString()
}

val generatedFilePath: String = "/dbdata/Tables.scala"
val flywayDbName: String = "bootstrapplay2"

val dbConf = settingKey[DbConf]("Typesafe config file with slick settings")
val generateTables = taskKey[Seq[File]]("Generate slick code")

def createDbConf(dbConfFile: File): DbConf = {
  val configFactory = ConfigFactory.parseFile(dbConfFile)
  val configPath = s"$flywayDbName"
  val config = configFactory.getConfig(configPath).resolve
  val url =  s"${config.getString("database.urlPrefix")}${ config.getString("database.host")}:${config.getString("database.port")}/${config.getString("database.db")}"
  DbConf(
    config.getString("profile"),
    config.getString("database.driver"),
    config.getString("database.user"),
    config.getString("database.password"),
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

lazy val slickGen = taskKey[Seq[File]]("slickGen")
slickGen := Def.taskDyn(generateTablesTask((dbConf  in Global).value)).value

lazy val root = (project in file("."))
  .dependsOn(slick)
  .enablePlugins(PlayScala, SbtReactiveAppPlugin)
  .settings(
    dbConfSettings,
    libraryDependencies ++= Dependencies.list
  )
  .settings(Seq(
    maintainer := "innFactory",
    version := buildVersion,
    packageName := "innfactory-bootstrap-play2/bootstrap-play2",
    endpoints += HttpEndpoint("http", HttpIngress(Vector(80, 443), Vector.empty, Vector.empty)),
    deployMinikubeRpArguments ++= Vector(
      "--ingress-annotation", "ingress.kubernetes.io/rewrite-target=/",
      "--ingress-annotation", "nginx.ingress.kubernetes.io/rewrite-target=/"
    ),
    dockerUpdateLatest := latest, //change to latest val
    dockerRepository := Some("eu.gcr.io"),
    dockerExposedPorts := Seq(9000, 9000)
  ))

def generateTablesTask(conf: DbConf) = Def.task {
  val dir = sourceManaged.value
  val outputDir = (dir / "slick").getPath
  val fname = outputDir + generatedFilePath
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

coverageExcludedPackages += "<empty>;Reverse.*;router.*;.*AuthService.*;models\\\\.data\\\\..*;dbdata.Tables*;common.jwt.*;common.errorHandling.*;common.jwt.JwtFilter;db.codegen.*"

// Commands

addCommandAlias("ciTests", "; clean; coverage; flyway/flywayMigrate; test; coverageReport")
