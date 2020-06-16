package db

import java.util.UUID
import common.results.Results.Result
import common.results.errors.Errors.{ DatabaseError, NotFound }
import db.codegen.XPostgresProfile
import javax.inject.{ Inject, Singleton }
import slick.jdbc.JdbcBackend.Database
import play.api.libs.json.Json
import models.api.{ Company => CompanyObject }
import dbdata.Tables
import org.joda.time.DateTime
import models.api.Company.patch

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.implicitConversions

/**
 * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
 */
trait CompaniesDAO {

  def lookup(id: UUID): Future[Result[CompanyObject]]

  def internal_lookupByEmail(email: String): Future[Option[CompanyObject]]

  def create(CompanyObject: CompanyObject): Future[Result[CompanyObject]]

  def update(CompanyObject: CompanyObject): Future[Result[CompanyObject]]

  def delete(id: UUID): Future[Result[Boolean]]

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
class SlickCompaniesDAO @Inject()(db: Database)(implicit ec: ExecutionContext) extends CompaniesDAO with Tables {

  override val profile: XPostgresProfile.type = XPostgresProfile

  import profile.api._

  private val slickLocation = "SlickCompaniesDAO"

  private val queryById = Compiled((id: Rep[UUID]) => Company.filter(_.id === id))

  private val queryByEmail = Compiled(
    (email: Rep[String]) =>
      Company.filter(cs => {
        // email === firebaseUser.any is like calling .includes(email)
        email === cs.firebaseUser.any
      })
  )

  /**
   * Lookup single object
   * @param id
   * @return
   */
  def lookup(id: UUID): Future[Result[CompanyObject]] =
    db.run(queryById(id).result.headOption).map {
      case Some(row) =>
        Right(companyRowToCompanyObject(row))
      case None =>
        Left(
          NotFound()
        )
    }

  /**
   * Lookup Company by Email
   * @param email
   * @return
   */
  def internal_lookupByEmail(email: String): Future[Option[CompanyObject]] = {
    val f: Future[Option[CompanyRow]] =
      db.run(queryByEmail(email).result.headOption)
    f.map {
      case Some(row) =>
        Some(companyRowToCompanyObject(row))
      case None =>
        None
    }
  }

  /**
   * Patch object
   * @param CompanyObject
   * @return
   */
  def update(CompanyObject: CompanyObject): Future[Result[CompanyObject]] =
    (db
      .run(queryById(CompanyObject.id.getOrElse(UUID.randomUUID())).result.headOption))
      .map {
        case Some(option: CompanyRow) => {
          val oldObject     = companyRowToCompanyObject(option)
          val patchedObject = patch(CompanyObject, oldObject)
          db.run(
              queryById(CompanyObject.id.getOrElse(UUID.randomUUID()))
                .update(companyObjectToCompanyRow(patchedObject))
            )
            .map {
              case 0 =>
                Left(
                  DatabaseError("Could not replace entity", slickLocation, "update", "row not updated")
                )
              case _ => Right(patchedObject)
            }
        }
        case None =>
          Future(
            Left(
              DatabaseError("Could not find entity to update", slickLocation, "update", "entity not found")
            )
          )
      }
      .flatten

  /**
   * Delete Object
   * @param id
   * @return
   */
  def delete(id: UUID): Future[Result[Boolean]] = {
    for {
      dbQueryResult <- db.run(queryById(id).result.headOption)
    } yield
      for {
        dbDeleteResult <- db.run(queryById(id).delete)
      } yield {
        dbDeleteResult
      } match {
        case 0 =>
          Left(
            DatabaseError("could not delete entity", slickLocation, "delete", "entity was deleted")
          )
        case _ => Right(true)
      }
  }.flatten

  /**
   * Create new Object
   * @param CompanyObject
   * @return
   */
  def create(CompanyObject: CompanyObject): Future[Result[CompanyObject]] =
    try {
      val entityToSave = companyObjectToCompanyRow(CompanyObject)
      val action       = (Company returning Company) += entityToSave
      val actionResult = db.run(action)
      actionResult.map { createdObject =>
        Right(companyRowToCompanyObject(createdObject))
      }
    } catch {
      case _: Throwable =>
        Future(
          Left(
            DatabaseError("failed to create", slickLocation, "create", "could not create entity")
          )
        )
    }

  private def companyObjectToCompanyRow(CompanyObject: CompanyObject): CompanyRow =
    CompanyRow(
      id = CompanyObject.id.getOrElse(UUID.randomUUID()),
      firebaseUser = CompanyObject.firebaseUser.getOrElse(List.empty[String]),
      settings = CompanyObject.settings.getOrElse(Json.parse("{}")),
      created = CompanyObject.created.getOrElse(DateTime.now),
      updated = CompanyObject.updated.getOrElse(DateTime.now)
    )

  private def companyRowToCompanyObject(companyRow: CompanyRow): CompanyObject =
    CompanyObject(
      id = Some(companyRow.id),
      firebaseUser = Some(companyRow.firebaseUser),
      settings = Some(companyRow.settings),
      created = Some(companyRow.created),
      updated = Some(companyRow.updated)
    )

  /**
   * Close db
   * @return
   */
  def close(): Future[Unit] =
    Future.successful(db.close())

}
