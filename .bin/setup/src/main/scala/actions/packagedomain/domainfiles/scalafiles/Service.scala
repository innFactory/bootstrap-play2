package actions.packagedomain.domainfiles.scalafiles

import config.SetupConfig

case class Service(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def subPath =
    s"/$packageName/domain/interfaces/"
  val name = s"${packageDomain}Service"
  override def getContent()(implicit config: SetupConfig): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    val domainService = DomainService(packageDomain, packageName)
    s"""
       |package ${config.project.packagesRoot}.$packageName.domain.interfaces
       |
       |import com.google.inject.ImplementedBy
       |import ${config.project.packagesRoot}.$packageName.domain.services.${domainService.name}
       |import ${config.project.packagesRoot}.$packageName.domain.models.{${domainModelId.name}, ${domainModel.name}}
       |import de.innfactory.play.controller.ResultStatus
       |import cats.data.EitherT
       |import ${config.project.packagesRoot}.commons.RequestContextWithUser
       |
       |import scala.concurrent.Future
       |
       |@ImplementedBy(classOf[${domainService.name}])
       |trait $name {
       |  def getAll()(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Seq[${domainModel.name}]]
       |  def getById(id: ${domainModelId.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, ${domainModel.name}]
       |  def update(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, ${domainModel.name}]
       |  def create(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, ${domainModel.name}]
       |  def delete(id: ${domainModelId.name})(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Boolean]
       |}
       |""".stripMargin
  }
}
