package actions.packagedomain.domainfiles.scalafiles

import config.SetupConfig

case class SlickRepository(packageDomain: String, packageName: String) extends ScalaDomainFile {
  override def path()(implicit config: SetupConfig) = s"${System.getProperty("user.dir")}/$packageName/infrastructure/"
  val name = s"Slick${packageDomain}Repository"
  override def getContent(): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    val repository = Repository(packageDomain, packageName)

    s"""
       |package de.innfactory.bootstrapplay2.$packageName.infrastructure
       |
       |import cats.data.{EitherT, Validated}
       |import dbdata.Tables
       |import de.innfactory.play.controller.ResultStatus
       |import de.innfactory.bootstrapplay2.commons.infrastructure.BaseSlickRepository
       |import de.innfactory.bootstrapplay2.$packageName.domain.interfaces.${repository.name}
       |import de.innfactory.bootstrapplay2.$packageName.domain.models.{${domainModel.name}, ${domainModelId.name}
       |import de.innfactory.bootstrapplay2.$packageName.infrastructure.mapper.CompanyMapper._
       |import de.innfactory.play.db.codegen.XPostgresProfile.api._
       |import slick.jdbc.JdbcBackend.Database
       |import slick.jdbc.{ResultSetConcurrency, ResultSetType}
       |import de.innfactory.play.slick.enhanced.query.EnhancedQuery._
       |import de.innfactory.play.smithy4play.TraceContext
       |
       |import javax.inject.Inject
       |import scala.concurrent.{ExecutionContext, Future}
       |
       |private[$packageName] class $name @Inject() (db: Database)(implicit ec: ExecutionContext)
       |    extends BaseSlickRepository(db)
       |    with ${repository.name} {
       |
       |  private val queryById = (id: ${domainModelId.name}) => Compiled(Tables.${domainModel.name}.filter(_.id === id.value))
       |
       |
       |  override def getAll()(implicit rc: TraceContext): EitherT[Future, ResultStatus, Seq[${domainModel.name}]] =
       |    lookupSequenceGeneric(Tables.${domainModel.name}.result)
       |
       |  override def getById(id: ${domainModelId.name})(implicit
       |      rc: TraceContext
       |  ): EitherT[Future, ResultStatus, ${domainModel.name}] =
       |    lookupGeneric(queryById(id).result.headOption)
       |
       |  override def create(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: TraceContext): EitherT[Future, ResultStatus, ${domainModel.name}] =
       |    createGeneric(
       |      ${domainModel.nameLowerCased()},
       |      row => (Tables.${domainModel.name} returning Tables.${domainModel.name}) += row
       |    )
       |
       |  def update(${domainModel
        .nameLowerCased()}: ${domainModel.name})(implicit rc: TraceContext): EitherT[Future, ResultStatus, ${domainModel.name}] =
       |    for {
       |      updated <-
       |        updateGeneric(
       |          queryById(${domainModel.nameLowerCased()}.id).result.headOption,
       |          (updated: ${domainModel.name}) => Tables.${domainModel.name} insertOrUpdate ${domainModel
        .nameLowerCased()}To${domainModel.name}Row(updated),
       |          (old: ${domainModel.name}) => old.patch(${domainModel.nameLowerCased()})
       |        )
       |    } yield updated
       |
       |  def delete(id: ${domainModelId.name})(implicit rc: TraceContext): EitherT[Future, ResultStatus, Boolean] =
       |    deleteGeneric(
       |      queryById(id).result.headOption,
       |      queryById(id).delete
       |    )
       |}
       |
       |""".stripMargin
  }
}
