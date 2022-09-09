package packagedomain.scalafiles

import packagedomain.common.CrudHelper
import packagedomain.smithyfiles.ApiDefinition
import config.SetupConfig

case class ApplicationMapper(packageDomain: String, packageName: String) extends ScalaDomainFile with CrudHelper {
  override def subPath =
    s"/$packageName/application/mapper/"
  val name = s"${packageDomain}Mapper"
  override def getContent(withCrud: Boolean)(implicit config: SetupConfig): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    val apiDefinition = ApiDefinition(packageDomain, packageName)

    val content = s"""
       |package ${config.project.getNamespace()}.$packageName.application.mapper
       |
       |import ${config.project.getNamespace()}.application.controller.BaseMapper
       |${CrudImportsKey}
       |
       |trait $name extends BaseMapper {
       |  ${CrudLogicKey}
       |}
       |""".stripMargin

    replaceForCrud(
      content,
      withCrud,
      createCrudLogic(domainModel, domainModelId, apiDefinition),
      createCrudImports(domainModel, domainModelId, apiDefinition)
    )
  }

  private def createCrudImports(domainModel: DomainModel, domainModelId: DomainModelId, apiDefinition: ApiDefinition)(
      implicit config: SetupConfig
  ): String =
    s"""
       |import ${config.project.getNamespace()}.$packageName.domain.models.{${domainModel.name}, ${domainModelId.name}}
       |import ${config.project
        .getNamespace()}.api.{${apiDefinition.responsesName}, ${apiDefinition.requestBodyName}, ${apiDefinition.responseName}}
       |import io.scalaland.chimney.dsl.TransformerOps
       |import org.joda.time.DateTime
       |""".stripMargin

  private def createCrudLogic(
      domainModel: DomainModel,
      domainModelId: DomainModelId,
      apiDefinition: ApiDefinition
  ): String =
    s"""
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
       |""".stripMargin
}
