package actions.packagedomain.domainfiles.scalafiles

import actions.packagedomain.domainfiles.common.CrudHelper
import config.SetupConfig

case class SlickRepository(packageDomain: String, packageName: String) extends ScalaDomainFile with CrudHelper {
  override def subPath =
    s"/$packageName/infrastructure/"
  val name = s"Slick${packageDomain}Repository"
  override def getContent(withCrud: Boolean)(implicit config: SetupConfig): String = {
    val domainModel = DomainModel(packageDomain, packageName)
    val domainModelId = DomainModelId(packageDomain, packageName)
    val repository = Repository(packageDomain, packageName)
    val slickMapper = SlickMapper(packageDomain, packageName)

    val content = s"""
       |package ${config.project.packagesRoot}.$packageName.infrastructure
       |
       |import dbdata.Tables
       |import ${config.project.packagesRoot}.commons.infrastructure.BaseSlickRepository
       |import ${config.project.packagesRoot}.$packageName.domain.interfaces.${repository.name}
       |import de.innfactory.play.db.codegen.XPostgresProfile.api._
       |import slick.jdbc.JdbcBackend.Database
       |${CrudImportsKey}
       |
       |import javax.inject.Inject
       |import scala.concurrent.{ExecutionContext, Future}
       |
       |private[$packageName] class $name @Inject() (db: Database)(implicit ec: ExecutionContext)
       |    extends BaseSlickRepository(db)
       |    with ${repository.name} {
       |  ${CrudLogicKey}
       |}
       |
       |""".stripMargin

    replaceForCrud(
      content,
      withCrud,
      createCrudLogic(domainModel, domainModelId),
      createCrudImports(domainModel, domainModelId, slickMapper)
    )
  }

  private def createCrudImports(domainModel: DomainModel, domainModelId: DomainModelId, slickMapper: SlickMapper)(
      implicit config: SetupConfig
  ): String =
    s"""
       |import ${config.project.packagesRoot}.$packageName.domain.models.{${domainModel.name}, ${domainModelId.name}}
       |import ${config.project.packagesRoot}.$packageName.infrastructure.mapper.${slickMapper.name}._
       |import de.innfactory.play.controller.ResultStatus
       |import de.innfactory.play.smithy4play.TraceContext
       |import cats.data.EitherT
       |""".stripMargin

  private def createCrudLogic(domainModel: DomainModel, domainModelId: DomainModelId): String =
    s"""
       |  private val queryById = (id: ${domainModelId.name}) => Compiled(Tables.${domainModel.name}.filter(_.id === id.value))
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
       |""".stripMargin
}
