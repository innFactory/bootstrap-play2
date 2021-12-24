package de.innfactory.bootstrapplay2.users.infrastructure

import akka.NotUsed
import akka.stream.scaladsl.Source
import de.innfactory.bootstrapplay2.commons.results.Results.Result
import de.innfactory.bootstrapplay2.users.domain.interfaces.{UserRepository, UserService}
import de.innfactory.bootstrapplay2.users.domain.models.{Claims, User, UserId}

import scala.concurrent.{ExecutionContext, Future}

class UserRepositoryMock extends UserRepository {

  private def defaultUser(userId: UserId = UserId("userId")) =
    User(
      userId = userId,
      email = "email",
      emailVerified = true,
      disabled = false,
      claims = Claims(),
      displayName = Some("defaultUser"),
      lastSignIn = None,
      lastRefresh = None,
      creation = None
    )

  def getAllUsersAsSource: Source[User, NotUsed] = Source.apply(Seq.empty[User])

  def getUserByEmail(email: String): Result[User] = Right(defaultUser())

  def getUserById(userId: UserId)(implicit ec: ExecutionContext): Future[Result[User]] = Future(
    Right(defaultUser(userId))
  )

  def createUser(email: String): Result[User] = Right(defaultUser())

  def upsertUser(user: User, oldUser: User): Result[User] = Right(defaultUser())

  def setUserClaims(userId: UserId, claims: Claims): Result[Boolean] = Right(true)

  def setUserPassword(userId: UserId, newPassword: String): Result[User] = Right(defaultUser())

  def setEmailVerifiedState(userId: UserId, state: Boolean): Result[Boolean] = Right(true)

  def setUserDisabledState(userId: UserId, state: Boolean): Result[Boolean] = Right(true)

  def setUserDisplayName(userId: UserId, displayName: String): Result[Boolean] = Right(true)

  def setEmailVerified(userId: UserId): Result[Boolean] = Right(true)
}
