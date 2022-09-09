package packagedomain.scalafiles

import packagedomain.common.CrudHelper
import config.SetupConfig

case class DomainService(packageDomain: String, packageName: String) extends ScalaDomainFile with CrudHelper {
  override def subPath =
    s"/$packageName/domain/services/"
  val name = s"Domain${packageDomain}Service"
  override def getContent(withCrud: Boolean)(implicit config: SetupConfig): String = {
    val domainModelId = DomainModelId(packageDomain, packageName)
    val domainModel = DomainModel(packageDomain, packageName)
    val service = Service(packageDomain, packageName)
    val repository = Repository(packageDomain, packageName)

    val content = s"""
       |package ${config.project.getNamespace()}.$packageName.domain.services
       |
       |${CrudImportsKey}
       |import ${config.project.getNamespace()}.$packageName.domain.interfaces.{${repository.name}, ${service.name}}
       |import javax.inject.Inject
       |import scala.concurrent.{ExecutionContext, Future}
       |
       |private[companies] class $name @Inject() (${repository.nameLowerCased()}: ${repository.name})(implicit
       |    ec: ExecutionContext
       |) extends ${service.name} {
       |  ${CrudLogicKey}
       |}
       |""".stripMargin

    replaceForCrud(
      content,
      withCrud,
      createCrudLogic(domainModel, domainModelId, repository),
      createCrudImports(domainModel, domainModelId)
    )
  }

  private def createCrudImports(domainModel: DomainModel, domainModelId: DomainModelId)(implicit
      config: SetupConfig
  ): String =
    s"""
       |import de.innfactory.play.controller.ResultStatus
       |import cats.data.EitherT
       |import ${config.project.getNamespace()}.commons.RequestContextWithUser
       |import ${config.project.getNamespace()}.$packageName.domain.models.{${domainModelId.name}, ${domainModel.name}}
       |""".stripMargin

  private def createCrudLogic(domainModel: DomainModel, domainModelId: DomainModelId, repository: Repository): String =
    s"""
       |  def getAll()(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Seq[${domainModel.name}]] =
       |    ${repository.nameLowerCased()}.getAll()
       |
       |  def getById(id: ${domainModelId.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, ${domainModel.name}] =
       |    ${repository.nameLowerCased()}.getById(id)
       |
       |  def update(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, ${domainModel.name}] =
       |    ${repository.nameLowerCased()}.update(${domainModel.nameLowerCased()})
       |
       |  def create(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, ${domainModel.name}] =
       |    ${repository.nameLowerCased()}.create(${domainModel.nameLowerCased()})
       |
       |  def delete(id: ${domainModelId.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Boolean] =
       |    ${repository.nameLowerCased()}.delete(id)
       |""".stripMargin
}
