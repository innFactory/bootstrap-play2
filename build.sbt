import com.typesafe.config.ConfigFactory
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerEntrypoint
import sbt.{Def, Resolver, _}
//settings

name := """bootstrap-play2"""
scalaVersion := Dependencies.scalaVersion

resolvers += Resolver.githubPackages("innFactory")

val token = sys.env.getOrElse("GITHUB_TOKEN", "")

val githubSettings = Seq(
   githubOwner := "innFactory",
    githubRepository := "bootstrap-play2",
  credentials :=
    Seq(Credentials(
      "GitHub Package Registry",
      "maven.pkg.github.com",
      "innFactory",
      token
    ))
)

val latest = sys.env.get("BRANCH") match {
  case Some(str) => if (str.equals("master")) true else false
  case None      => false
}

val buildVersion = sys.env.get("VERSION") match {
  case Some(str) => str
  case None      => version.key.toString()
}

val dockerRegistry = sys.env.get("DOCKER_REGISTRY") match {
  case Some(repo) => Some(repo)
  case None       => Some("localhost")
}

val generatedFilePath: String = "/dbdata/Tables.scala"
val flywayDbName: String      = "bootstrap-play2"
val dbConf                    = settingKey[DbConf]("Typesafe config file with slick settings")
val generateTables            = taskKey[Seq[File]]("Generate slick code")

// Testing

coverageExcludedPackages += "<empty>;Reverse.*;router.*;.*AuthService.*;models\\\\.data\\\\..*;dbdata.Tables*;de.innfactory.bootstrapplay2.common.jwt.*;de.innfactory.bootstrapplay2.common.errorHandling.*;de.innfactory.bootstrapplay2.common.jwt.JwtFilter;db.codegen.*;de.innfactory.bootstrapplay2.common.pubSub.*;publicmetrics.influx.*"
Test / fork  := true

// Commands

addCommandAlias("ciTests", "; clean; coverage; flyway/flywayMigrate; test; coverageReport")
addCommandAlias("localTests", "; clean; flyway/flywayMigrate; test")

/* TaskKeys */
lazy val slickGen = taskKey[Seq[File]]("slickGen")

/* Create db config for flyway */
def createDbConf(dbConfFile: File): DbConf = {
  val configFactory = ConfigFactory.parseFile(dbConfFile)
  val configPath    = s"$flywayDbName"
  val config        = configFactory.getConfig(configPath).resolve
  val url           = s"${config.getString("database.urlPrefix")}${config
    .getString("database.host")}:${config.getString("database.port")}/${config.getString("database.db")}"
  DbConf(
    config.getString("profile"),
    config.getString("database.driver"),
    config.getString("database.user"),
    config.getString("database.password"),
    url
  )
}

def dbConfSettings =
  Seq(
    Global / dbConf := createDbConf((Compile / resourceDirectory).value / "application.conf")
  )

def flywaySettings =
  Seq(
    flywayUrl := (Global / dbConf).value.url,
    flywayUser := (Global / dbConf).value.user,
    flywayPassword := (Global / dbConf).value.password,
    flywaySchemas := (Seq("postgis"))
  )

def generateTablesTask(conf: DbConf) =
  Def.task {
    val dir          = sourceManaged.value
    val outputDir    = (dir / "slick/main").getPath
    val fname        = outputDir + generatedFilePath
    val generator    = "db.codegen.CustomizedCodeGenerator"
    val url          = conf.url
    val slickProfile = conf.profile.dropRight(1)
    val jdbcDriver   = conf.driver
    val pkg          = "db.Tables"
    val cp           = (Compile / dependencyClasspath).value
    val username     = conf.user
    val password     = conf.password
    val s            = streams.value
    val r            = (Compile / runner).value
    r.run(
      generator,
      cp.files,
      Array(outputDir, slickProfile, jdbcDriver, url, pkg, username, password),
      s.log
    )
    Seq(file(fname))
  }

slickGen := Def.taskDyn(generateTablesTask((Global / dbConf).value)).value

/*project definitions*/

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, DockerPlugin, SwaggerPlugin)
  .dependsOn(slick)
  .settings(
    scalaVersion := Dependencies.scalaVersion,
    dbConfSettings,
    libraryDependencies ++= Dependencies.list,
    // Adding Cache
    libraryDependencies ++= Seq(ehcache),
    dependencyOverrides += Dependencies.sl4j, // Override to avoid problems with HikariCP 4.x
    swaggerDomainNameSpaces := Seq(
      "models",
    ), // New Models have to be added here to be referencable in routes
    swaggerPrettyJson := true,
    swaggerV3 := true,
    githubSettings
  )
  .settings(
    Seq(
      maintainer := "innFactory",
      version := buildVersion,
      Docker / packageName := "bootstrap-play2",
      dockerUpdateLatest := latest,
      dockerRepository := dockerRegistry,
      dockerExposedPorts := Seq(8080, 8080),
      dockerEntrypoint := Seq(""),
      dockerBaseImage := "openjdk:11.0.6-jre-slim",
      dockerEntrypoint := Seq("/opt/docker/bin/bootstrap-play2", "-Dplay.server.pidfile.path=/dev/null")
    )
  )

lazy val flyway = (project in file("modules/flyway"))
  .enablePlugins(FlywayPlugin)
  .settings(
    scalaVersion := Dependencies.scalaVersion,
    libraryDependencies ++= Dependencies.list,
    flywaySettings,
    githubSettings
  )

lazy val slick = (project in file("modules/slick"))
  .settings(
    scalaVersion := Dependencies.scalaVersion,
    libraryDependencies ++= Dependencies.list,
    githubSettings
  )

lazy val globalResources = file("conf")

/* Scala format */
ThisBuild / scalafmtOnCompile := true // all projects

/* Change compiling */
Compile / sourceGenerators += Def.taskDyn(generateTablesTask((Global / dbConf).value)).taskValue
Compile /compile  := {
  (Compile / compile).value
}
