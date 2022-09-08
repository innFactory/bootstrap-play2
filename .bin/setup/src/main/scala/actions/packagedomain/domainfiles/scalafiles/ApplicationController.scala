package actions.packagedomain.domainfiles.scalafiles

import actions.packagedomain.domainfiles.smithyfiles.ApiDefinition
import config.SetupConfig

case class ApplicationController(packageDomain: String, packageName: String) extends ScalaDomainFile {
  val name = s"${packageDomain}Controller"
  override def subPath = s"/$packageName/application/"

  override def getContent()(implicit config: SetupConfig): String = {
    val applicationMapper = ApplicationMapper(packageDomain, packageName)
    val service = Service(packageDomain, packageName)
    val apiDefinition = ApiDefinition(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)

    s"""
      |package ${config.project.packagesRoot}.$packageName.application
      |
      |import ${config.project.packagesRoot}.application.controller.BaseController
      |import ${config.project.packagesRoot}.$packageName.application.mapper.${applicationMapper.name}
      |import ${config.project.packagesRoot}.$packageName.domain.interfaces.${service.name}
      |import ${config.project.packagesRoot}.$packageName.domain.models.${domainModelId.name}
      |import ${config.project.packagesRoot}.api.{${apiDefinition.responsesName}, ${apiDefinition.name}, ${apiDefinition.requestBodyName}, ${apiDefinition.responseName}}
      |import de.innfactory.play.smithy4play.ImplicitLogContext
      |import play.api.mvc.ControllerComponents
      |import de.innfactory.smithy4play.{AutoRouting, ContextRoute}
      |import play.api.Application
      |
      |import javax.inject.{Inject, Singleton}
      |import scala.concurrent.ExecutionContext
      |
      |@AutoRouting
      |@Singleton
      |class $name @Inject() (
      |    ${service.nameLowerCased()}: ${service.name}
      |)(implicit
      |    ec: ExecutionContext,
      |    cc: ControllerComponents,
      |    app: Application
      |) extends BaseController
      |    with ImplicitLogContext
      |    with ${apiDefinition.name}[ContextRoute]
      |    with ${applicationMapper.name} {
      |    
      |  override def get${packageDomain}ById(${domainModelId
        .nameLowerCased()}: String): ContextRoute[${apiDefinition.responseName}] =
      |    Endpoint.withAuth
      |      .execute(${service.nameLowerCased()}.getById(${domainModelId.name}(${domainModelId.nameLowerCased()}))(_))
      |      .complete
      |
      |  override def getAll${packageName.capitalize}(): ContextRoute[${apiDefinition.responsesName}] =
      |    Endpoint.withAuth
      |      .execute(${service.nameLowerCased()}.getAll()(_))
      |      .complete
      |
      |  override def create$packageDomain(body: ${apiDefinition.requestBodyName}): ContextRoute[${apiDefinition.responseName}] = Endpoint.withAuth
      |    .execute(${service.nameLowerCased()}.create(body)(_))
      |    .complete
      |
      |  override def update$packageDomain(body: ${apiDefinition.requestBodyName}): ContextRoute[${apiDefinition.responseName}] = Endpoint.withAuth
      |    .execute(${service.nameLowerCased()}.update(body)(_))
      |    .complete
      |
      |  override def delete$packageDomain(${domainModelId
        .nameLowerCased()}: String): ContextRoute[Unit] = Endpoint.withAuth
      |    .execute(${service.nameLowerCased()}.delete(${domainModelId.name}(${domainModelId.nameLowerCased()}))(_))
      |    .complete
      |    }
      |""".stripMargin
  }
}
