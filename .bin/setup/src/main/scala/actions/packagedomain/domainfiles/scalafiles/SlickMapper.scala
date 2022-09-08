package actions.packagedomain.domainfiles.scalafiles

import config.SetupConfig

case class SlickMapper(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def subPath =
    s"/$packageName/infrastructure/mapper/"
  val name = s"${packageDomain}Mapper"
  override def getContent()(implicit config: SetupConfig): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    s"""
       |package ${config.project.packagesRoot}.$packageName.infrastructure.mapper
       |
       |import dbdata.Tables
       |import ${config.project.packagesRoot}.$packageName.domain.models.{${domainModel.name}, ${domainModelId.name}}
       |import io.scalaland.chimney.dsl.TransformerOps
       |import org.joda.time.DateTime
       |
       |private[infrastructure] object $name {
       |
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
       |}
       |""".stripMargin
  }
}
