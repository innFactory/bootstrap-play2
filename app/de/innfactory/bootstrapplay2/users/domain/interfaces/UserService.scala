package de.innfactory.bootstrapplay2.users.domain.interfaces

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.commons.{RequestContextWithUser, TraceContext}
import de.innfactory.bootstrapplay2.commons.results.Results.Result
import de.innfactory.bootstrapplay2.users.domain.models.{Claims, User, UserId}
import de.innfactory.bootstrapplay2.users.domain.services.DomainUserService
import scala.concurrent.Future

@ImplementedBy(classOf[DomainUserService])
trait UserService {

  def sendPasswordResetToken()(implicit rc: RequestContextWithUser): Future[Result[Unit]]

  def getAllUsersAsSource(implicit rc: RequestContextWithUser): Source[User, NotUsed]

  def getUserByEmail(email: String)(implicit rc: RequestContextWithUser): Result[User]

  def getUserById(userId: UserId)(rc: RequestContextWithUser): Future[Result[User]]

  def getUserByIdWithoutRequestContext(userId: UserId): Future[Result[User]]

  def createUser(email: String)(implicit rc: RequestContextWithUser): Result[User]

  def upsertUser(user: User, oldUser: User)(implicit rc: RequestContextWithUser): Result[User]

  def setUserClaims(userId: UserId, claims: Claims)(implicit rc: RequestContextWithUser): Result[Boolean]

  def setUserPassword(userId: UserId, newPassword: String)(implicit rc: TraceContext): Result[User]

  def setEmailVerifiedState(userId: UserId, state: Boolean)(implicit rc: RequestContextWithUser): Result[Boolean]

  def setUserDisabledState(userId: UserId, state: Boolean)(implicit rc: RequestContextWithUser): Result[Boolean]

  def setUserDisplayName(userId: UserId, displayName: String)(implicit rc: RequestContextWithUser): Result[Boolean]

  def setEmailVerified(userId: UserId)(implicit rc: RequestContextWithUser): Result[Boolean]

}
