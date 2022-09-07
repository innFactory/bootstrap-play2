package actions.packagedomain.domainfiles.scalafiles

import config.SetupConfig

case class DomainModel(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def path()(implicit config: SetupConfig) = s"${System.getProperty("user.dir")}/$packageName/domain/models/"
  val name = s"${packageDomain}"
  override def getContent(): String = {
    val domainModelId = DomainModelId(packageDomain, packageName)
    s"""
      |package de.innfactory.bootstrapplay2.$packageName.domain.models
      |
      |import org.joda.time.DateTime
      |
      |case class $name(
      |    id: ${domainModelId.name},
      |    created: DateTime,
      |    updated: DateTime
      |) {
      |  def patch(newObject: $name): $name =
      |    newObject.copy(
      |      id = this.id,
      |      created = this.created,
      |      updated = DateTime.now
      |    )
      |}
      |""".stripMargin
  }
}
