package actions.packagedomain.domainfiles.scalafiles

import config.SetupConfig

case class DomainService(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def subPath =
    s"/$packageName/domain/services/"
  val name = s"Domain${packageDomain}Service"
  override def getContent()(implicit config: SetupConfig): String = {
    val domainModelId = DomainModelId(packageDomain, packageName)
    val domainModel = DomainModel(packageDomain, packageName)
    val service = Service(packageDomain, packageName)
    val repository = Repository(packageDomain, packageName)

    s"""
       |package ${config.project.packagesRoot}.$packageName.domain.services
       |
       |import ${config.project.packagesRoot}.$packageName.domain.interfaces.{${repository.name}, ${service.name}}
       |import ${config.project.packagesRoot}.$packageName.domain.models.{${domainModelId.name}, ${domainModel.name}}
       |import de.innfactory.play.controller.ResultStatus
       |import cats.data.EitherT
       |import ${config.project.packagesRoot}.commons.RequestContextWithUser
       |
       |import javax.inject.Inject
       |import scala.concurrent.{ExecutionContext, Future}
       |
       |private[companies] class $name @Inject() (${repository.nameLowerCased()}: ${repository.name})(implicit
       |    ec: ExecutionContext
       |) extends ${service.name} {
       |
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
       |}
       |""".stripMargin
  }
}
