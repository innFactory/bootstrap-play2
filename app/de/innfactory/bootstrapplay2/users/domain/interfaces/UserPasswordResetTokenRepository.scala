package de.innfactory.bootstrapplay2.users.domain.interfaces

import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.commons.TraceContext
import de.innfactory.bootstrapplay2.commons.results.Results.Result
import de.innfactory.bootstrapplay2.users.domain.models.{UserId, UserPasswordResetToken}
import de.innfactory.bootstrapplay2.users.infrastructure.SlickUserResetTokenRepository

import scala.concurrent.Future

@ImplementedBy(classOf[SlickUserResetTokenRepository])
trait UserPasswordResetTokenRepository {
  def getForUser(userId: UserId)(implicit rc: TraceContext): Future[Result[UserPasswordResetToken]]

  def create(entity: UserPasswordResetToken)(implicit rc: TraceContext): Future[Result[UserPasswordResetToken]]

  def delete(entity: UserPasswordResetToken)(implicit rc: TraceContext): Future[Result[Int]]
}
