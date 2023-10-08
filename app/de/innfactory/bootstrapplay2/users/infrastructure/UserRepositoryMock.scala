package de.innfactory.bootstrapplay2.users.infrastructure

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.EitherT
import cats.implicits.catsSyntaxEitherId
import de.innfactory.bootstrapplay2.users.domain.interfaces.UserRepository
import de.innfactory.bootstrapplay2.users.domain.models.{Claims, User, UserId}
import de.innfactory.play.controller.ResultStatus

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserRepositoryMock @Inject() (implicit ec: ExecutionContext) extends UserRepository {

  private def defaultUser(userId: UserId = UserId("userId")) =
    User(
      userId = userId,
      email = "email",
      emailVerified = true,
      disabled = false,
      claims = Claims(),
      firstName = None,
      lastName = None,
      displayName = Some("defaultUser"),
      lastSignIn = None,
      lastRefresh = None,
      creation = None
    )

  def getAllUsersAsSource: Source[User, NotUsed] = Source.apply(Seq.empty[User])

  def getUserByEmail(email: String): EitherT[Future, ResultStatus, User] =
    EitherT.rightT(defaultUser())

  def getUserById(userId: UserId): EitherT[Future, ResultStatus, User] = EitherT.rightT(
    defaultUser(userId)
  )

  def createUser(email: String): EitherT[Future, ResultStatus, User] =
    EitherT.rightT(defaultUser())

  def upsertUser(user: User, oldUser: User): EitherT[Future, ResultStatus, User] = EitherT.rightT(
    defaultUser()
  )

  def setUserClaims(userId: UserId, claims: Claims): EitherT[Future, ResultStatus, Boolean] = EitherT.rightT(
    true
  )

  def setUserPassword(userId: UserId, newPassword: String): EitherT[Future, ResultStatus, User] = EitherT.rightT(
    defaultUser()
  )

  def setEmailVerifiedState(userId: UserId, state: Boolean): EitherT[Future, ResultStatus, Boolean] = EitherT.rightT(
    true
  )

  def setUserDisabledState(userId: UserId, state: Boolean): EitherT[Future, ResultStatus, Boolean] = EitherT.rightT(
    true
  )

  def setUserDisplayName(userId: UserId, displayName: String): EitherT[Future, ResultStatus, Boolean] = EitherT.rightT(
    true
  )

  def setEmailVerified(userId: UserId): EitherT[Future, ResultStatus, Boolean] =
    EitherT.rightT(true)
}
