package de.innfactory.bootstrapplay2.users.domain.interfaces

import cats.data.EitherT
import com.google.inject.ImplementedBy
import de.innfactory.play.smithy4play.TraceContext
import de.innfactory.bootstrapplay2.users.domain.models.{UserId, UserPasswordResetToken}
import de.innfactory.bootstrapplay2.users.infrastructure.SlickUserResetTokenRepository
import de.innfactory.play.controller.ResultStatus

import scala.concurrent.Future

@ImplementedBy(classOf[SlickUserResetTokenRepository])
trait UserPasswordResetTokenRepository {
  def getForUser(userId: UserId)(implicit rc: TraceContext): EitherT[Future, ResultStatus, UserPasswordResetToken]

  def create(entity: UserPasswordResetToken)(implicit
      rc: TraceContext
  ): EitherT[Future, ResultStatus, UserPasswordResetToken]

  def delete(entity: UserPasswordResetToken)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Int]
}
