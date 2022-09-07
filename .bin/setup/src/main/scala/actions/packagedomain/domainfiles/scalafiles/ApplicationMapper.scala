package actions.packagedomain.domainfiles.scalafiles

import actions.packagedomain.domainfiles.smithyfiles.ApiDefinition
import config.SetupConfig

case class ApplicationMapper(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def path()(implicit config: SetupConfig) =
    s"${System.getProperty("user.dir")}/$packageName/application/mapper/"
  val name = s"${packageDomain}Mapper"
  override def getContent(): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    val apiDefinition = ApiDefinition(packageDomain, packageName)

    s"""
       |package de.innfactory.bootstrapplay2.$packageName.application.mapper
       |
       |import de.innfactory.bootstrapplay2.application.controller.BaseMapper
       |import de.innfactory.bootstrapplay2.$packageName.domain.models.{${domainModel.name}, ${domainModelId.name}}
       |import de.innfactory.bootstrapplay2.api.{${apiDefinition.responsesName}, ${apiDefinition.name}, ${apiDefinition.requestBodyName}, ${apiDefinition.responseName}}
       |import io.scalaland.chimney.dsl.TransformerOps
       |import org.joda.time.DateTime
       |
       |import java.util.UUID
       |
       |trait $name extends BaseMapper {
       |  implicit val ${domainModel
        .nameLowerCased()}To${apiDefinition.responsesName}: ${domainModel.name} => ${apiDefinition.responsesName} = (
       |    ${domainModel.nameLowerCased()}: ${domainModel.name}
       |  ) =>
       |    ${domainModel.nameLowerCased()}
       |      .into[${apiDefinition.responsesName}]
       |      .withFieldComputed(_.id, c => c.id.value)
       |      .withFieldComputed(_.created, c => dateTimeToDateWithTime(c.created))
       |      .withFieldComputed(_.updated, c => dateTimeToDateWithTime(c.updated))
       |      .transform
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
