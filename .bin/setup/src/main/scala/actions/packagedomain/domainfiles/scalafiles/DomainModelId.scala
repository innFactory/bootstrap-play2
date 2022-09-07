package actions.packagedomain.domainfiles.scalafiles

import config.SetupConfig

case class DomainModelId(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def path()(implicit config: SetupConfig) = s"${System.getProperty("user.dir")}/$packageName/domain/models/"
  val name = s"${packageDomain}Id"
  override def getContent(): String =
    s"""
      |package de.innfactory.bootstrapplay2.$packageName.domain.models
      |
      |import java.util.UUID
      |
      |case class $name(value: String)
      |
      |object $name {
      |  def create: $name = $name(UUID.randomUUID().toString)
      |}
      |""".stripMargin
}
