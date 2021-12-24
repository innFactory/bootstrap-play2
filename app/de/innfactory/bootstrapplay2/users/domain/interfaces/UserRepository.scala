package de.innfactory.bootstrapplay2.users.domain.interfaces

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.commons.results.Results.Result
import de.innfactory.bootstrapplay2.users.domain.models.{Claims, User, UserId}
import de.innfactory.bootstrapplay2.users.infrastructure.UserRepositoryMock

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[UserRepositoryMock])
trait UserRepository {
  def getAllUsersAsSource: Source[User, NotUsed]

  def getUserByEmail(email: String): Result[User]

  def getUserById(userId: UserId)(implicit ec: ExecutionContext): Future[Result[User]]

  def createUser(email: String): Result[User]

  def upsertUser(user: User, oldUser: User): Result[User]

  def setUserClaims(userId: UserId, claims: Claims): Result[Boolean]

  def setUserPassword(userId: UserId, newPassword: String): Result[User]

  def setEmailVerifiedState(userId: UserId, state: Boolean): Result[Boolean]

  def setUserDisabledState(userId: UserId, state: Boolean): Result[Boolean]

  def setUserDisplayName(userId: UserId, displayName: String): Result[Boolean]

  def setEmailVerified(userId: UserId): Result[Boolean]
}
