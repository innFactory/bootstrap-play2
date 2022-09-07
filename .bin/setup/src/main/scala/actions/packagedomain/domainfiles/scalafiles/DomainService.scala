package actions.packagedomain.domainfiles.scalafiles

import config.SetupConfig

case class DomainService(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def path()(implicit config: SetupConfig) = s"${System.getProperty("user.dir")}/$packageName/domain/services/"
  val name = s"Domain${packageDomain}Service"
  override def getContent(): String = {
    val domainModelId = DomainModelId(packageDomain, packageName)
    val domainModel = DomainModel(packageDomain, packageName)
    val service = Service(packageDomain, packageName)
    val repository = Repository(packageDomain, packageName)

    s"""
       |package de.innfactory.bootstrapplay2.$packageName.domain.services
       |
       |import de.innfactory.bootstrapplay2.$packageName.domain.interfaces.{${repository.name}, ${service.name}}
       |import de.innfactory.bootstrapplay2.$packageName.domain.models.{${domainModelId.name}, ${domainModel.name}
       |import de.innfactory.play.controller.ResultStatus
       |import cats.data.EitherT
       |import de.innfactory.bootstrapplay2.commons.{RequestContext, RequestContextWithUser}
       |
       |import javax.inject.Inject
       |import scala.concurrent.{ExecutionContext, Future}
       |
       |private[companies] class $name @Inject() (${repository.nameLowerCased()}: ${repository.name})(implicit
       |    ec: ExecutionContext
       |) extends ${service.name} {
       |
       |  def getAll()(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Seq[${domainModel.name}]] =
       |    ${repository.name}.getAll()
       |
       |  def getById(id: ${domainModelId.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, ${domainModel.name}] =
       |    ${repository.name}.getById(id)
       |
       |  def update(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, ${domainModel.name}] =
       |    ${repository.name}.update(company)
       |
       |  def create(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, ${domainModel.name}] =
       |    ${repository.name}.create(${domainModel.nameLowerCased()})
       |
       |  def delete(id: ${domainModelId.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Boolean] =
       |    ${repository.name}.delete(id)
       |}
       |""".stripMargin
  }
}
