package packagedomain.scalafiles

import packagedomain.common.CrudHelper
import config.SetupConfig

case class Service(packageDomain: String, packageName: String) extends ScalaDomainFile with CrudHelper {
  override def subPath =
    s"/$packageName/domain/interfaces/"
  val name = s"${packageDomain.capitalize}Service"
  override def getContent(withCrud: Boolean)(implicit config: SetupConfig): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    val domainService = DomainService(packageDomain, packageName)
    val content = s"""
       |package ${config.project.getNamespace()}.$packageName.domain.interfaces
       |
       |${CrudImportsKey}
       |import com.google.inject.ImplementedBy
       |import ${config.project.getNamespace()}.$packageName.domain.services.${domainService.name}
       |
       |import scala.concurrent.Future
       |
       |@ImplementedBy(classOf[${domainService.name}])
       |trait $name {
       |   ${CrudLogicKey}
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
       |import ${config.project.getNamespace()}.$packageName.domain.models.{${domainModelId.name}, ${domainModel.name}}
       |import de.innfactory.play.controller.ResultStatus
       |import cats.data.EitherT
       |import ${config.project.getNamespace()}.commons.RequestContextWithUser
       |""".stripMargin

  private def createCrudLogic(domainModel: DomainModel, domainModelId: DomainModelId): String =
    s"""
       |  def getAll()(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Seq[${domainModel.name}]]
       |  def getById(id: ${domainModelId.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, ${domainModel.name}]
       |  def update(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, ${domainModel.name}]
       |  def create(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, ${domainModel.name}]
       |  def delete(id: ${domainModelId.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Boolean]
       |""".stripMargin
}
