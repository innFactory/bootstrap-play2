package actions.packagedomain.domainfiles.scalafiles

import config.SetupConfig

case class Repository(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def subPath =
    s"/$packageName/domain/interfaces/"
  val name = s"${packageDomain}Repository"
  override def getContent()(implicit config: SetupConfig): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    val domainRepository = SlickRepository(packageDomain, packageName)
    s"""
       |package ${config.project.packagesRoot}.$packageName.domain.interfaces
       |
       |import com.google.inject.ImplementedBy
       |import ${config.project.packagesRoot}.$packageName.infrastructure.${domainRepository.name}
       |import ${config.project.packagesRoot}.$packageName.domain.models.{${domainModel.name}, ${domainModelId.name}}
       |import de.innfactory.play.controller.ResultStatus
       |import cats.data.EitherT
       |import de.innfactory.play.smithy4play.TraceContext
       |
       |import scala.concurrent.Future
       |
       |@ImplementedBy(classOf[${domainRepository.name}])
       |private[$packageName] trait $name {
       |  def getAll()(implicit rc: TraceContext): EitherT[Future, ResultStatus, Seq[${domainModel.name}]]
       |  def getById(id: ${domainModelId.name})(implicit rc: TraceContext): EitherT[Future, ResultStatus, ${domainModel.name}]
       |  def create(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: TraceContext): EitherT[Future, ResultStatus, ${domainModel.name}]
       |  def update(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: TraceContext): EitherT[Future, ResultStatus, ${domainModel.name}]
       |  def delete(id: ${domainModelId.name})(implicit rc: TraceContext): EitherT[Future, ResultStatus, Boolean]
       |}
       |""".stripMargin
  }
}
