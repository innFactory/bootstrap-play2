package de.innfactory.bootstrapplay2.users.domain.interfaces

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.EitherT
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.commons.RequestContextWithUser
import de.innfactory.bootstrapplay2.users.domain.models.{Claims, User, UserId}
import de.innfactory.bootstrapplay2.users.domain.services.DomainUserService
import de.innfactory.play.controller.ResultStatus
import de.innfactory.play.tracing.TraceContext

import scala.concurrent.Future

@ImplementedBy(classOf[DomainUserService])
trait UserService {

  def sendPasswordResetToken()(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Unit]

  def getAllUsersAsSource(implicit rc: RequestContextWithUser): Source[User, NotUsed]

  def getUserByEmail(email: String)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, User]

  def getUserById(userId: UserId)(rc: RequestContextWithUser): EitherT[Future, ResultStatus, User]

  def getUserByIdWithoutRequestContext(userId: UserId): EitherT[Future, ResultStatus, User]

  def createUser(email: String)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, User]

  def upsertUser(user: User, oldUser: User)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, User]

  def setUserClaims(userId: UserId, claims: Claims)(implicit
      rc: RequestContextWithUser
  ): EitherT[Future, ResultStatus, Boolean]

  def setUserPassword(userId: UserId, newPassword: String)(implicit
      rc: TraceContext
  ): EitherT[Future, ResultStatus, User]

  def setEmailVerifiedState(userId: UserId, state: Boolean)(implicit
      rc: RequestContextWithUser
  ): EitherT[Future, ResultStatus, Boolean]

  def setUserDisabledState(userId: UserId, state: Boolean)(implicit
      rc: RequestContextWithUser
  ): EitherT[Future, ResultStatus, Boolean]

  def setUserDisplayName(userId: UserId, displayName: String)(implicit
      rc: RequestContextWithUser
  ): EitherT[Future, ResultStatus, Boolean]

  def setEmailVerified(userId: UserId)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Boolean]

}
