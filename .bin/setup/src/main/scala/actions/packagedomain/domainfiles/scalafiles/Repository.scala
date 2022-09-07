package actions.packagedomain.domainfiles.scalafiles

import config.SetupConfig

case class Repository(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def path()(implicit config: SetupConfig) =
    s"${System.getProperty("user.dir")}/$packageName/domain/interfaces/"
  val name = s"${packageDomain}Repository"
  override def getContent(): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    val domainRepository = SlickRepository(packageDomain, packageName)
    s"""
       |package de.innfactory.bootstrapplay2.$packageName.domain.interfaces
       |
       |import com.google.inject.ImplementedBy
       |import de.innfactory.bootstrapplay2.$packageName.infrastructure.${domainRepository.name}
       |import de.innfactory.bootstrapplay2.$packageName.domain.models.{${domainModel.name}, ${domainModelId.name}}
       |import cats.data.EitherT
       |import de.innfactory.play.smithy4play.TraceContext
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
