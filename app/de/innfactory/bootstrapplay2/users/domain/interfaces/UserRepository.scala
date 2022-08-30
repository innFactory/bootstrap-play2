package de.innfactory.bootstrapplay2.users.domain.interfaces

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.EitherT
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.users.domain.models.{Claims, User, UserId}
import de.innfactory.bootstrapplay2.users.infrastructure.UserRepositoryMock
import de.innfactory.play.controller.ResultStatus

import scala.concurrent.Future

@ImplementedBy(classOf[UserRepositoryMock])
trait UserRepository {
  def getAllUsersAsSource: Source[User, NotUsed]

  def getUserByEmail(email: String): EitherT[Future, ResultStatus, User]

  def getUserById(userId: UserId): EitherT[Future, ResultStatus, User]

  def createUser(email: String): EitherT[Future, ResultStatus, User]

  def upsertUser(user: User, oldUser: User): EitherT[Future, ResultStatus, User]

  def setUserClaims(userId: UserId, claims: Claims): EitherT[Future, ResultStatus, Boolean]

  def setUserPassword(userId: UserId, newPassword: String): EitherT[Future, ResultStatus, User]

  def setEmailVerifiedState(userId: UserId, state: Boolean): EitherT[Future, ResultStatus, Boolean]

  def setUserDisabledState(userId: UserId, state: Boolean): EitherT[Future, ResultStatus, Boolean]

  def setUserDisplayName(userId: UserId, displayName: String): EitherT[Future, ResultStatus, Boolean]

  def setEmailVerified(userId: UserId): EitherT[Future, ResultStatus, Boolean]
}
