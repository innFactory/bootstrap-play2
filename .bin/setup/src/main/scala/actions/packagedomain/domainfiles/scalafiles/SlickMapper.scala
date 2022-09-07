package actions.packagedomain.domainfiles.scalafiles

import config.SetupConfig

case class SlickMapper(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def path()(implicit config: SetupConfig) =
    s"${System.getProperty("user.dir")}/$packageName/infrastructure/mapper/"
  val name = s"${packageDomain}Mapper.scala"
  override def getContent(): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    s"""
       |package de.innfactory.bootstrapplay2.$packageName.infrastructure.mapper
       |
       |import dbdata.Tables
       |import de.innfactory.bootstrapplay2.$packageName.domain.models.{${domainModel.name}, ${domainModelId.name}
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
