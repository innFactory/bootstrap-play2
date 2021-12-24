package de.innfactory.bootstrapplay2.users.infrastructure

import cats.implicits.catsSyntaxEitherId
import dbdata.Tables
import de.innfactory.play.db.codegen.XPostgresProfile.api._
import de.innfactory.bootstrapplay2.commons.results.Results.Result
import de.innfactory.play.controller.ResultStatus
import de.innfactory.bootstrapplay2.commons.TraceContext
import de.innfactory.bootstrapplay2.commons.infrastructure.BaseSlickDAO
import de.innfactory.bootstrapplay2.users.domain.interfaces.UserPasswordResetTokenRepository
import de.innfactory.bootstrapplay2.users.domain.models.{UserId, UserPasswordResetToken}
import de.innfactory.bootstrapplay2.users.infrastructure.mappers.UserPasswordResetTokenMapper._
import play.api.inject.ApplicationLifecycle

import javax.inject.{Inject, Singleton}
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

class SlickUserResetTokenRepository @Inject() (db: Database, lifecycle: ApplicationLifecycle)(implicit
    ec: ExecutionContext
) extends BaseSlickDAO(db)
    with UserPasswordResetTokenRepository {

  def getForUser(userId: UserId)(implicit rc: TraceContext): Future[Result[UserPasswordResetToken]] =
    lookupGeneric(
      Tables.UserPasswordResetTokens.filter(_.userId === userId.value).result.headOption
    )

  def create(
      entity: UserPasswordResetToken
  )(implicit rc: TraceContext): Future[Result[UserPasswordResetToken]] =
    db.run(
      Tables.UserPasswordResetTokens insertOrUpdate entity
    ).map(_.asRight[ResultStatus].map(_ => entity))

  def delete(entity: UserPasswordResetToken)(implicit rc: TraceContext): Future[Result[Int]] =
    db.run(Tables.UserPasswordResetTokens.filter(_.userId === entity.userId.value).delete).map(_.asRight[ResultStatus])

  lifecycle.addStopHook(() =>
    Future.successful {
      close()
    }
  )
}
