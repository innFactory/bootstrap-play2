package packagedomain.domainfiles.scalafiles

import config.SetupConfig

case class DomainModel(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def subPath =
    s"/$packageName/domain/models/"
  val name = s"${packageDomain}"
  override def getContent(withCrud: Boolean)(implicit config: SetupConfig): String = {
    val domainModelId = DomainModelId(packageDomain, packageName)
    s"""
      |package ${config.project.packagesRoot}.$packageName.domain.models
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
