package packagedomain.scalafiles

import packagedomain.common.CrudHelper
import config.SetupConfig

case class SlickMapper(packageDomain: String, packageName: String) extends ScalaDomainFile with CrudHelper {
  override def subPath =
    s"/$packageName/infrastructure/mapper/"
  val name = s"${packageDomain}Mapper"
  override def getContent(withCrud: Boolean)(implicit config: SetupConfig): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    val content = s"""
       |package ${config.project.getNamespace()}.$packageName.infrastructure.mapper
       |
       |import dbdata.Tables
       |${CrudImportsKey}
       |
       |private[infrastructure] object $name {
       |  ${CrudLogicKey}
       |}
       |""".stripMargin

    replaceForCrud(
      content,
      withCrud,
      createCrudLogic(domainModel, domainModelId),
      createCrudImports(domainModel, domainModelId)
    )
  }

  private def createCrudImports(domainModel: DomainModel, domainModelId: DomainModelId)(implicit
      config: SetupConfig
  ): String =
    s"""
       |import ${config.project.getNamespace()}.$packageName.domain.models.{${domainModel.name}, ${domainModelId.name}}
       |import io.scalaland.chimney.dsl.TransformerOps
       |""".stripMargin

  private def createCrudLogic(domainModel: DomainModel, domainModelId: DomainModelId): String =
    s"""
       |  implicit def ${domainModel
        .nameLowerCased()}RowTo${domainModel.name}(row: Tables.${domainModel.name}Row): ${domainModel.name} =
       |    row
       |      .into[${domainModel.name}]
       |      .withFieldComputed(_.id, r => ${domainModelId.name}(r.id))
       |      .transform
       |
       |  implicit def ${domainModel.nameLowerCased()}To${domainModel.name}Row(${domainModel
        .nameLowerCased()}: ${domainModel.name}): Tables.${domainModel.name}Row =
       |    ${domainModel.nameLowerCased()}
       |      .into[Tables.${domainModel.name}Row]
       |      .withFieldComputed[String, String](_.id, c => c.id.value)
       |      .transform
       |""".stripMargin
}
