package config

import config.SetupConfig.{ProjectConfig, SmithyConfig}
import play.api.libs.json.{Json, OFormat}

import scala.io.Source

case class SetupConfig(project: ProjectConfig, smithy: SmithyConfig)

object SetupConfig {
  implicit val format: OFormat[SetupConfig] = Json.format[SetupConfig]

  def get(): SetupConfig = {
    val configString = Source.fromResource("conf.json").getLines().mkString(" ")
    Json.parse(configString).as[SetupConfig]
  }

  case class ProjectConfig(sourcesRoot: String, packagesRoot: String) {
    def getPackagePath() = s"$sourcesRoot/${packagesRoot.replace('.', '/')}"
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
