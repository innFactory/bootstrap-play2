package config

import config.SetupConfig.{ProjectConfig, SmithyConfig}
import play.api.libs.json.{Json, OFormat}

import java.nio.file.{Files, Path, Paths}
import scala.io.Source

case class SetupConfig(project: ProjectConfig, smithy: SmithyConfig)

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

  case class ProjectConfig(sourcesRoot: String, domain: String, name: String) {
    def getPackagePath() = s"$sourcesRoot/${domain.replace('.', '/')}/${name.replaceAll("-", "")}"
    def getNamespace() = s"$domain.${name.replaceAll("-", "")}"
  }
  object ProjectConfig {
    implicit val format: OFormat[ProjectConfig] = Json.format[ProjectConfig]
  }
  case class SmithyConfig(sourcesRoot: String, apiDefinitionRoot: String) {
    def getPath() = s"${sourcesRoot.replace('.', '/')}/${apiDefinitionRoot.replace('.', '/')}"
  }
  object SmithyConfig {
    implicit val format: OFormat[SmithyConfig] = Json.format[SmithyConfig]
  }
}
