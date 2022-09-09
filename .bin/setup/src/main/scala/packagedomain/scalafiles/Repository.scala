package packagedomain.scalafiles

import packagedomain.common.CrudHelper
import packagedomain.smithyfiles.ApiDefinition
import config.SetupConfig

case class Repository(packageDomain: String, packageName: String) extends ScalaDomainFile with CrudHelper {
  override def subPath =
    s"/$packageName/domain/interfaces/"
  val name = s"${packageDomain}Repository"
  override def getContent(withCrud: Boolean)(implicit config: SetupConfig): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    val domainRepository = SlickRepository(packageDomain, packageName)
    val content = s"""
       |package ${config.project.getNamespace()}.$packageName.domain.interfaces
       |
       |import com.google.inject.ImplementedBy
       |import ${config.project.getNamespace()}.$packageName.infrastructure.${domainRepository.name}
       |
       |@ImplementedBy(classOf[${domainRepository.name}])
       |private[$packageName] trait $name {

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
       |import ${config.project.getNamespace()}.$packageName.domain.models.{${domainModel.name}, ${domainModelId.name}}
       |import de.innfactory.play.controller.ResultStatus
       |import cats.data.EitherT
       |import de.innfactory.play.smithy4play.TraceContext
       |import scala.concurrent.Future
       |""".stripMargin

  private def createCrudLogic(domainModel: DomainModel, domainModelId: DomainModelId): String =
    s"""
       |  def getAll()(implicit rc: TraceContext): EitherT[Future, ResultStatus, Seq[${domainModel.name}]]
       |  def getById(id: ${domainModelId.name})(implicit rc: TraceContext): EitherT[Future, ResultStatus, ${domainModel.name}]
       |  def create(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: TraceContext): EitherT[Future, ResultStatus, ${domainModel.name}]
       |  def update(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: TraceContext): EitherT[Future, ResultStatus, ${domainModel.name}]
       |  def delete(id: ${domainModelId.name})(implicit rc: TraceContext): EitherT[Future, ResultStatus, Boolean]
       |""".stripMargin

}
