package actions.packagedomain.domainfiles.scalafiles

import config.SetupConfig

case class Service(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def path()(implicit config: SetupConfig) =
    s"${System.getProperty("user.dir")}/$packageName/domain/interfaces/"
  val name = s"${packageDomain}Service"
  override def getContent(): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    val domainService = DomainService(packageDomain, packageName)
    s"""
       |package de.innfactory.bootstrapplay2.$packageName.domain.interfaces
       |
       |import com.google.inject.ImplementedBy
       |import de.innfactory.bootstrapplay2.$packageName.domain.services.${domainService.name}
       |import de.innfactory.bootstrapplay2.$packageName.domain.models.{${domainModelId.name}, ${domainModel.name}
       |import de.innfactory.play.controller.ResultStatus
       |import cats.data.EitherT
       |import de.innfactory.bootstrapplay2.commons.{RequestContext, RequestContextWithUser}
       |
       |@ImplementedBy(classOf[${domainService.name}])
       |trait $name {
       |
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
