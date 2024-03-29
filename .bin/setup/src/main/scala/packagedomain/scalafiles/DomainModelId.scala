package packagedomain.scalafiles

import config.SetupConfig

case class DomainModelId(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def subPath =
    s"/$packageName/domain/models/"
  val name = s"${packageDomain.capitalize}Id"
  override def getContent(withCrud: Boolean)(implicit config: SetupConfig): String =
    s"""
      |package ${config.project.getNamespace()}.$packageName.domain.models
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
