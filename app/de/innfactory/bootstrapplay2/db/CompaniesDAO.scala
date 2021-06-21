package de.innfactory.bootstrapplay2.db

import java.util.UUID
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.common.request.RequestContext
import de.innfactory.bootstrapplay2.db.BaseSlickDAO
import de.innfactory.bootstrapplay2.common.results.Results.Result
import de.innfactory.bootstrapplay2.common.results.errors.Errors.{ DatabaseResult, NotFound }
import de.innfactory.play.db.codegen.XPostgresProfile

import javax.inject.{ Inject, Singleton }
import slick.jdbc.JdbcBackend.Database
import play.api.libs.json.Json
import de.innfactory.bootstrapplay2.models.api.{ Company => CompanyObject }
import org.joda.time.DateTime
import de.innfactory.bootstrapplay2.models.api.Company.patch
import de.innfactory.bootstrapplay2.repositories.LocationRepositoryImpl
import de.innfactory.play.slick.enhanced.utils.filteroptions.FilterOptions
import de.innfactory.play.slick.enhanced.query.EnhancedQuery._
import dbdata.Tables
import de.innfactory.bootstrapplay2.common.logging.ImplicitLogContext
import slick.basic.DatabasePublisher
import slick.dbio.{ DBIOAction, NoStream }
import slick.jdbc.{ ResultSetConcurrency, ResultSetType }

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.implicitConversions

@ImplementedBy(classOf[SlickCompaniesSlickDAO])
trait CompaniesDAO {

  def lookup(id: Long)(implicit rc: RequestContext): Future[Result[CompanyObject]]

  def all(implicit rc: RequestContext): Future[Seq[CompanyObject]]

  def allWithFilter(filter: Seq[FilterOptions[Tables.Company, _]])(implicit
    rc: RequestContext
  ): Future[Seq[CompanyObject]]

  def streamedAll(implicit rc: RequestContext): DatabasePublisher[CompanyObject]

  def internal_lookupByEmail(email: String)(implicit rc: RequestContext): Future[Option[CompanyObject]]

  def create(CompanyObject: CompanyObject)(implicit rc: RequestContext): Future[Result[CompanyObject]]

  def update(CompanyObject: CompanyObject)(implicit rc: RequestContext): Future[Result[CompanyObject]]

  def delete(id: Long)(implicit rc: RequestContext): Future[Result[Boolean]]

  def close(): Future[Unit]
}

@Singleton
class SlickCompaniesSlickDAO @Inject() (db: Database)(implicit ec: ExecutionContext)
    extends BaseSlickDAO(db)
    with CompaniesDAO
    with ImplicitLogContext {

  override val profile = XPostgresProfile
  import profile.api._

  /* - - - Compiled Queries - - - */

  private val queryById = Compiled((id: Rep[Long]) => Tables.Company.filter(_.id === id))

  private val queryByEmail = Compiled((email: Rep[String]) =>
    Tables.Company.filter { cs =>
      // email === firebaseUser.any is like calling .includes(email)
      email === cs.firebaseUser.any
    }
  )

  def lookup(id: Long)(implicit rc: RequestContext): Future[Result[CompanyObject]] =
    lookupGeneric(
      queryById(id).result.headOption
    )

  def streamedAll(implicit rc: RequestContext): DatabasePublisher[CompanyObject] =
    db.stream(
      Tables.Company.result
        .withStatementParameters(
          rsType = ResultSetType.ForwardOnly,
          rsConcurrency = ResultSetConcurrency.ReadOnly,
          fetchSize = 1000
        )
        .transactionally
    ).mapResult(companyRowToCompanyObject)

  def all(implicit rc: RequestContext): Future[Seq[CompanyObject]] =
    lookupSequenceGenericRawSequence(
      Tables.Company.result
    )

  def allWithFilter(filter: Seq[FilterOptions[Tables.Company, _]])(implicit
    rc: RequestContext
  ): Future[Seq[CompanyObject]] = {
    println(filter)
    lookupSequenceGenericRawSequence(
      queryFromFiltersSeq(filter).result
    )(c => companyRowToCompanyObject(c.copy()), rc)
  }

  private def queryFromFiltersSeq(filter: Seq[FilterOptions[Tables.Company, _]]) =
    Compiled(Tables.Company.filterOptions(filter))

  def internal_lookupByEmail(email: String)(implicit rc: RequestContext): Future[Option[CompanyObject]] = {
    val f: Future[Option[Tables.CompanyRow]] =
      db.run(queryByEmail(email).result.headOption)
    f.map {
      case Some(row) =>
        Some(companyRowToCompanyObject(row))
      case None      =>
        None
    }
  }

  def update(companyObject: CompanyObject)(implicit rc: RequestContext): Future[Result[CompanyObject]] =
    updateGeneric(
      queryById(companyObject.id.getOrElse(0)).result.headOption,
      (toPatch: CompanyObject) => queryById(companyObject.id.getOrElse(0)).update(companyObjectToCompanyRow(toPatch)),
      (old: CompanyObject) => patch(companyObject, old)
    )

  def delete(id: Long)(implicit rc: RequestContext): Future[Result[Boolean]] =
    deleteGeneric(
      queryById(id).result.headOption,
      queryById(id).delete
    )

  def create(companyObject: CompanyObject)(implicit rc: RequestContext): Future[Result[CompanyObject]] =
    createGeneric(
      companyObject,
      queryById(companyObject.id.getOrElse(0)).result.headOption,
      (entityToSave: Tables.CompanyRow) => (Tables.Company returning Tables.Company) += entityToSave
    )

  /* - - - Mapper Functions - - - */

  implicit private def companyObjectToCompanyRow(companyObject: CompanyObject): Tables.CompanyRow =
    Tables.CompanyRow(
      id = companyObject.id.getOrElse(0),
      firebaseUser = companyObject.firebaseUser.getOrElse(List.empty[String]),
      settings = companyObject.settings.getOrElse(Json.parse("{}")),
      stringAttribute1 = companyObject.stringAttribute1,
      stringAttribute2 = companyObject.stringAttribute2,
      longAttribute1 = companyObject.longAttribute1,
      booleanAttribute = companyObject.booleanAttribute,
      created = companyObject.created.getOrElse(DateTime.now),
      updated = companyObject.updated.getOrElse(DateTime.now)
    )

  implicit private def companyRowToCompanyObject(companyRow: Tables.CompanyRow): CompanyObject =
    CompanyObject(
      id = Some(companyRow.id),
      firebaseUser = Some(companyRow.firebaseUser),
      settings = Some(companyRow.settings),
      stringAttribute1 = companyRow.stringAttribute1,
      stringAttribute2 = companyRow.stringAttribute2,
      longAttribute1 = companyRow.longAttribute1,
      booleanAttribute = companyRow.booleanAttribute,
      created = Some(companyRow.created),
      updated = Some(companyRow.updated)
    )

}
