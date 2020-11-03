import com.typesafe.config.ConfigFactory
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerEntrypoint
import sbt.{Def, Resolver, _}
//settings

name := """bootstrap-play2"""
scalaVersion := Dependencies.scalaVersion

resolvers += Resolver.githubPackages("innFactory")

githubTokenSource := TokenSource.Environment("GITHUB_TOKEN")
val token = sys.env.getOrElse("GITHUB_TOKEN", "5a5b5b1c28f2801a68eff3fb297ac6c9164c5bb1")

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

coverageExcludedPackages += "<empty>;Reverse.*;router.*;.*AuthService.*;models\\\\.data\\\\..*;dbdata.Tables*;common.jwt.*;common.errorHandling.*;common.jwt.JwtFilter;db.codegen.*;common.pubSub.*;publicmetrics.influx.*"
fork in Test := true

// Commands

addCommandAlias("ciTests", "; clean; coverage; flyway/flywayMigrate; test; coverageReport")
addCommandAlias("localTests", "; clean; flyway/flywayMigrate; test")

/* TaskKeys */
lazy val slickGen = taskKey[Seq[File]]("slickGen")
lazy val copyRes  = TaskKey[Unit]("copyRes")

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
    dbConf in Global := createDbConf((resourceDirectory in Compile).value / "application.conf")
  )

def flywaySettings =
  Seq(
    flywayUrl := (dbConf in Global).value.url,
    flywayUser := (dbConf in Global).value.user,
    flywayPassword := (dbConf in Global).value.password,
    flywaySchemas := (Seq("postgis"))
  )

def generateTablesTask(conf: DbConf) =
  Def.task {
    val dir          = sourceManaged.value
    val outputDir    = (dir / "slick").getPath
    val fname        = outputDir + generatedFilePath
    val generator    = "db.codegen.CustomizedCodeGenerator"
    val url          = conf.url
    val slickProfile = conf.profile.dropRight(1)
    val jdbcDriver   = conf.driver
    val pkg          = "db.Tables"
    val cp           = (dependencyClasspath in Compile).value
    val username     = conf.user
    val password     = conf.password
    val s            = streams.value
    val r            = (runner in Compile).value
    r.run(
      generator,
      cp.files,
      Array(outputDir, slickProfile, jdbcDriver, url, pkg, username, password),
      s.log
    )
    Seq(file(fname))
  }

slickGen := Def.taskDyn(generateTablesTask((dbConf in Global).value)).value

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
    swaggerDomainNameSpaces := Seq(
      "models",
      "publicmetrics"
    ), // New Models have to be added here to be referencable in routes
    swaggerPrettyJson := true,
    swaggerV3 := true,
    githubSettings
  )
  .settings(
    Seq(
      maintainer := "innFactory",
      version := buildVersion,
      packageName in Docker := "bootstrap-play2",
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

unmanagedResourceDirectories in Compile += globalResources
unmanagedResourceDirectories in Runtime += globalResources

/* Scala format */
scalafmtOnCompile in ThisBuild := true // all projects

/* Change compiling */
sourceGenerators in Compile += Def.taskDyn(generateTablesTask((dbConf in Global).value)).taskValue
compile in Compile := {
  (compile in Compile).value
}
