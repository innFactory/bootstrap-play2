package config

import config.SetupConfig.{BootstrapConfig, ProjectConfig, SmithyConfig}
import play.api.libs.json.{Json, OFormat}

import java.nio.file.{Files, Path, Paths}
import scala.io.Source

case class SetupConfig(project: ProjectConfig, bootstrap: BootstrapConfig) {
  val smithy: SmithyConfig = SmithyConfig()
}

object SetupConfig {
  implicit val format: OFormat[SetupConfig] = Json.format[SetupConfig]
  val pathToProjectSetupConf = s"${System.getProperty("user.dir")}/.bin/setup/"
  val fileName = "conf.json"

  def getFullPath(): Path = Paths.get(pathToProjectSetupConf + fileName)

  def get(): SetupConfig =
    if (Files.exists(getFullPath())) {
      val content = Files.readString(getFullPath())
      Json.parse(content).as[SetupConfig]
    } else {
      val defaultConfig = Source.fromResource(fileName).getLines().mkString(" ")
      Json.parse(defaultConfig).as[SetupConfig]
    }

  case class ProjectConfig(domain: String, name: String) {
    val sourcesRoot: String = "app"
    def getPackagePath() = s"$sourcesRoot/${domain.replace('.', '/')}/${name.replaceAll("-", "")}"
    def getNamespace() = s"$domain.${name.replaceAll("-", "")}"
  }
  object ProjectConfig {
    implicit val format: OFormat[ProjectConfig] = Json.format[ProjectConfig]
  }
  case class SmithyConfig(
  ) {
    val sourcesRoot: String = "modules/api-definition"
    val apiDefinitionRoot: String = "src.main.resources.META-INF.smithy"
    def getPath() = s"${sourcesRoot.replace('.', '/')}/${apiDefinitionRoot.replace('.', '/')}"
  }
  case class BootstrapConfig(paths: Seq[String])
  object BootstrapConfig {
    implicit val format: OFormat[BootstrapConfig] = Json.format[BootstrapConfig]
  }
}
