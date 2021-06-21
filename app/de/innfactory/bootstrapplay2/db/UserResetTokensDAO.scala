package de.innfactory.bootstrapplay2.db

import cats.implicits.catsSyntaxEitherId
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.common.request.RequestContext
import de.innfactory.play.db.codegen.XPostgresProfile
import de.innfactory.bootstrapplay2.common.results.Results.Result
import de.innfactory.bootstrapplay2.common.results.Results.ResultStatus
import javax.inject.{ Inject, Singleton }
import slick.jdbc.JdbcBackend.Database
import de.innfactory.bootstrapplay2.services.firebase.models.{
  UserPasswordResetTokens => UserPasswordResetTokensObject
}

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.implicitConversions

@ImplementedBy(classOf[SlickUserResetTokensSlickDAO])
trait UserResetTokensDAO {

  def getForUser(userId: String)(implicit rc: RequestContext): Future[Result[UserPasswordResetTokensObject]]

  def create(entity: UserPasswordResetTokensObject)(implicit
    rc: RequestContext
  ): Future[Result[UserPasswordResetTokensObject]]

  def delete(entity: UserPasswordResetTokensObject)(implicit rc: RequestContext): Future[Result[Int]]

  def close(): Future[Unit]
}

@Singleton
class SlickUserResetTokensSlickDAO @Inject() (db: Database)(implicit ec: ExecutionContext)
    extends BaseSlickDAO(db)
    with UserResetTokensDAO {

  override val profile = XPostgresProfile
  import profile.api._

  /* - - - Compiled Queries - - - */

  def getForUser(userId: String)(implicit rc: RequestContext): Future[Result[UserPasswordResetTokensObject]] =
    lookupGeneric(
      UserPasswordResetTokens.filter(_.userId === userId).result.headOption
    )

  def create(
    entity: UserPasswordResetTokensObject
  )(implicit rc: RequestContext): Future[Result[UserPasswordResetTokensObject]] =
    db.run(
      UserPasswordResetTokens insertOrUpdate entityToUserPasswordResetTokensRow(entity)
    ).map(_.asRight[ResultStatus].map(_ => entity))

  def delete(entity: UserPasswordResetTokensObject)(implicit rc: RequestContext): Future[Result[Int]] =
    db.run(UserPasswordResetTokens.filter(_.userId === entity.userId).delete).map(_.asRight[ResultStatus])

  /* - - - Mapper Functions - - - */

  implicit private def entityToUserPasswordResetTokensRow(
    entity: UserPasswordResetTokensObject
  ): UserPasswordResetTokensRow =
    UserPasswordResetTokensRow(
      userId = entity.userId,
      token = entity.token,
      created = entity.created,
      validUntil = entity.validUntil
    )

  implicit private def rowToUserPasswordResetTokensObject(
    row: UserPasswordResetTokensRow
  ): UserPasswordResetTokensObject =
    UserPasswordResetTokensObject(
      userId = row.userId,
      token = row.token,
      created = row.created,
      validUntil = row.validUntil
    )

}
