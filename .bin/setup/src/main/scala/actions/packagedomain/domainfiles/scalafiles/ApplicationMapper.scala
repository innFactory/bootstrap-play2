package actions.packagedomain.domainfiles.scalafiles

import actions.packagedomain.domainfiles.smithyfiles.ApiDefinition
import config.SetupConfig

case class ApplicationMapper(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def subPath =
    s"/$packageName/application/mapper/"
  val name = s"${packageDomain}Mapper"
  override def getContent()(implicit config: SetupConfig): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    val apiDefinition = ApiDefinition(packageDomain, packageName)

    s"""
       |package ${config.project.packagesRoot}.$packageName.application.mapper
       |
       |import ${config.project.packagesRoot}.application.controller.BaseMapper
       |import ${config.project.packagesRoot}.$packageName.domain.models.{${domainModel.name}, ${domainModelId.name}}
       |import ${config.project.packagesRoot}.api.{${apiDefinition.responsesName}, ${apiDefinition.requestBodyName}, ${apiDefinition.responseName}}
       |import io.scalaland.chimney.dsl.TransformerOps
       |import org.joda.time.DateTime
       |
       |trait $name extends BaseMapper {
       |  implicit val ${domainModel
        .nameLowerCased()}To${apiDefinition.responseName}: ${domainModel.name} => ${apiDefinition.responseName} = (
       |    ${domainModel.nameLowerCased()}: ${domainModel.name}
       |  ) =>
       |    ${domainModel.nameLowerCased()}
       |      .into[${apiDefinition.responseName}]
       |      .withFieldComputed(_.id, c => c.id.value)
       |      .withFieldComputed(_.created, c => dateTimeToDateWithTime(c.created))
       |      .withFieldComputed(_.updated, c => dateTimeToDateWithTime(c.updated))
       |      .transform
       |      
       |  implicit val ${domainModel
        .nameLowerCased()}To${apiDefinition.responsesName}: Seq[${domainModel.name}] => ${apiDefinition.responsesName} = ($packageName: Seq[${domainModel.name}]) =>
       |    ${apiDefinition.responsesName}($packageName.map(${domainModel
        .nameLowerCased()}To${apiDefinition.responseName}))
       |
       |  implicit val ${domainModel
        .nameLowerCased()}RequestBodyTo${domainModel.name}: ${apiDefinition.requestBodyName} => ${domainModel.name} = (requestBody: ${apiDefinition.requestBodyName}) =>
       |    requestBody
       |      .into[${domainModel.name}]
       |      .withFieldComputed(_.id, c => c.id.map(id => ${domainModelId.name}(id)).getOrElse(${domainModelId.name}.create))
       |      .withFieldConst(_.created, DateTime.now())
       |      .withFieldConst(_.updated, DateTime.now())
       |      .transform
       |}
       |""".stripMargin
  }
}
