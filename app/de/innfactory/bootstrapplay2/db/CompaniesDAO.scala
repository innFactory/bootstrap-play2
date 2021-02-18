package de.innfactory.bootstrapplay2.db

import java.util.UUID
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.common.request.{RequestContext, TraceContext}
import de.innfactory.bootstrapplay2.db.BaseSlickDAO
import de.innfactory.bootstrapplay2.common.results.Results.Result
import de.innfactory.bootstrapplay2.common.results.errors.Errors.{DatabaseResult, NotFound}
import de.innfactory.play.db.codegen.XPostgresProfile

import javax.inject.{Inject, Singleton}
import slick.jdbc.JdbcBackend.Database
import play.api.libs.json.Json
import de.innfactory.bootstrapplay2.models.api.{Company => CompanyObject}
import org.joda.time.DateTime
import de.innfactory.bootstrapplay2.models.api.Company.patch
import de.innfactory.bootstrapplay2.repositories.LocationRepositoryImpl
import de.innfactory.play.slick.enhanced.utils.filteroptions.FilterOptions
import de.innfactory.play.slick.enhanced.query.EnhancedQuery._
import dbdata.Tables
import de.innfactory.bootstrapplay2.common.logging.ImplicitLogContext
import slick.basic.DatabasePublisher
import slick.dbio.{DBIOAction, NoStream}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

/**
 * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
 */
@ImplementedBy(classOf[SlickCompaniesSlickDAO])
trait CompaniesDAO {

  def lookup(id: UUID)(implicit tc: TraceContext): Future[Result[CompanyObject]]

  def all(implicit tc: TraceContext): Future[Seq[CompanyObject]]

  def allWithFilter(filter: Seq[FilterOptions[Tables.Company, _]])(implicit
    tc: TraceContext
  ): Future[Seq[CompanyObject]]

  def internal_lookupByEmail(email: String)(implicit tc: TraceContext): Future[Option[CompanyObject]]

  def create(CompanyObject: CompanyObject)(implicit tc: TraceContext): Future[Result[CompanyObject]]

  def update(CompanyObject: CompanyObject)(implicit tc: TraceContext): Future[Result[CompanyObject]]

  def delete(id: UUID)(implicit tc: TraceContext): Future[Result[Boolean]]

  def close(): Future[Unit]
}

/**
 * A CompanyObject DAO implemented with Slick, leveraging Slick code gen.
 *
 * Note that you must run "flyway/flywayMigrate" before "compile" here.
 *
 * @param db the slick database that this CompanyObject DAO is using internally, bound through Module.
 * @param ec a CPU bound execution context.  Slick manages blocking JDBC calls with its
 *    own internal thread pool, so Play's default execution context is fine here.
 */
@Singleton
class SlickCompaniesSlickDAO @Inject() (db: Database)(implicit ec: ExecutionContext)
    extends BaseSlickDAO(db)
    with CompaniesDAO with ImplicitLogContext {

  // Class Name for identification in Database Errors
  override val currentClassForDatabaseError = "SlickCompaniesDAO"

  override val profile = XPostgresProfile
  import profile.api._

  /* - - - Compiled Queries - - - */

  private val queryById = Compiled((id: Rep[UUID]) => Tables.Company.filter(_.id === id))

  private val queryByEmail = Compiled((email: Rep[String]) =>
    Tables.Company.filter { cs =>
      // email === firebaseUser.any is like calling .includes(email)
      email === cs.firebaseUser.any
    }
  )

  /**
   * Lookup single object
   * @param id
   * @return
   */
  def lookup(id: UUID)(implicit tc: TraceContext): Future[Result[CompanyObject]] =
    lookupGeneric(
      queryById(id).result.headOption
    )

  def all(implicit tc: TraceContext): Future[Seq[CompanyObject]] =
    lookupSequenceGenericRawSequence(
      Tables.Company.result
    )

  def allWithFilter(filter: Seq[FilterOptions[Tables.Company, _]])(implicit
    tc: TraceContext
  ): Future[Seq[CompanyObject]] = {
    println(filter)
    lookupSequenceGenericRawSequence(
      queryFromFiltersSeq(filter).result
    )(c => companyRowToCompanyObject(c.copy()), tc)
  }

  private def queryFromFiltersSeq(filter: Seq[FilterOptions[Tables.Company, _]]) =
    Compiled(Tables.Company.filterOptions(filter))

  /**
   * Lookup Company by Email
   * @param email
   * @return
   */
  def internal_lookupByEmail(email: String)(implicit tc: TraceContext): Future[Option[CompanyObject]] = {
    val f: Future[Option[Tables.CompanyRow]] =
      db.run(queryByEmail(email).result.headOption)
    f.map {
      case Some(row) =>
        Some(companyRowToCompanyObject(row))
      case None      =>
        None
    }
  }

  /**
   * Patch object
   * @param companyObject
   * @return
   */
  def update(companyObject: CompanyObject)(implicit tc: TraceContext): Future[Result[CompanyObject]] =
    updateGeneric(
      queryById(companyObject.id.getOrElse(UUID.randomUUID())).result.headOption,
      (toPatch: CompanyObject) =>
        queryById(companyObject.id.getOrElse(UUID.randomUUID())).update(companyObjectToCompanyRow(toPatch)),
      (old: CompanyObject) => patch(companyObject, old)
    )

  /**
   * Delete Object
   * @param id
   * @return
   */
  def delete(id: UUID)(implicit tc: TraceContext): Future[Result[Boolean]] =
    deleteGeneric(
      queryById(id).result.headOption,
      queryById(id).delete
    )

  /**
   * Create new Object
   * @param companyObject
   * @return
   */
  def create(companyObject: CompanyObject)(implicit tc: TraceContext): Future[Result[CompanyObject]] =
    createGeneric(
      companyObject,
      queryById(companyObject.id.getOrElse(UUID.randomUUID())).result.headOption,
      (entityToSave: Tables.CompanyRow) => (Tables.Company returning Tables.Company) += entityToSave
    )

  /* - - - Mapper Functions - - - */

  implicit private def companyObjectToCompanyRow(companyObject: CompanyObject): Tables.CompanyRow =
    Tables.CompanyRow(
      id = companyObject.id.getOrElse(UUID.randomUUID()),
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
