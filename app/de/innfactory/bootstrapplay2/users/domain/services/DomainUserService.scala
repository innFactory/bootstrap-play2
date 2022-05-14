package de.innfactory.bootstrapplay2.users.domain.services

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.EitherT
import de.innfactory.bootstrapplay2.commons.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.commons.results.Results.Result
import de.innfactory.bootstrapplay2.commons.{RequestContextWithUser, TraceContext}
import de.innfactory.bootstrapplay2.users.domain.interfaces.{
  UserPasswordResetTokenRepository,
  UserRepository,
  UserService
}
import de.innfactory.bootstrapplay2.users.domain.models.{Claims, User, UserId, UserPasswordResetToken}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DomainUserService @Inject(
    userRepository: UserRepository,
    userPasswordResetTokenRepository: UserPasswordResetTokenRepository
)(implicit ec: ExecutionContext)
    extends UserService
    with ImplicitLogContext {

  def sendPasswordResetToken()(implicit rc: RequestContextWithUser): Future[Result[Unit]] = {
    val uiURI = "localhost"
    val result = for {
      token <- EitherT(
        userPasswordResetTokenRepository.create(
          UserPasswordResetToken(rc.user.userId)
        )
      )
      resetLink = s"$uiURI/reset?token=" + token.token + "&uid=" + rc.user.userId.value
      _ = rc.log.info(resetLink)
      // Optionally could Send Email _ <- EitherT(emailSendService.sendWelcomeEmail(createdUser.email, resetLink))
    } yield ()
    result.value
  }

  def getAllUsersAsSource(implicit rc: RequestContextWithUser): Source[User, NotUsed] =
    userRepository.getAllUsersAsSource

  def getUserByEmail(email: String)(implicit rc: RequestContextWithUser): Result[User] =
    userRepository.getUserByEmail(email)

  def getUserById(userId: UserId)(rc: RequestContextWithUser): Future[Result[User]] = userRepository.getUserById(userId)

  def getUserByIdWithoutRequestContext(userId: UserId): Future[Result[User]] = userRepository.getUserById(userId)

  def createUser(email: String)(implicit rc: RequestContextWithUser): Result[User] = userRepository.createUser(email)

  def upsertUser(user: User, oldUser: User)(implicit rc: RequestContextWithUser): Result[User] =
    userRepository.upsertUser(user, oldUser)

  def setUserClaims(userId: UserId, claims: Claims)(implicit rc: RequestContextWithUser): Result[Boolean] =
    userRepository.setUserClaims(userId, claims)

  def setUserPassword(userId: UserId, newPassword: String)(implicit rc: TraceContext): Result[User] =
    userRepository.setUserPassword(userId, newPassword)

  def setEmailVerifiedState(userId: UserId, state: Boolean)(implicit rc: RequestContextWithUser): Result[Boolean] =
    userRepository.setEmailVerifiedState(userId, state)

  def setUserDisabledState(userId: UserId, state: Boolean)(implicit rc: RequestContextWithUser): Result[Boolean] =
    userRepository.setUserDisabledState(userId, state)

  def setUserDisplayName(userId: UserId, displayName: String)(implicit rc: RequestContextWithUser): Result[Boolean] =
    userRepository.setUserDisplayName(userId, displayName)

  def setEmailVerified(userId: UserId)(implicit rc: RequestContextWithUser): Result[Boolean] =
    userRepository.setEmailVerified(userId)
}
