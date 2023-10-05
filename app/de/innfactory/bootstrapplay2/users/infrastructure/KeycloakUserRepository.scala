package de.innfactory.bootstrapplay2.users.infrastructure

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import cats.data.EitherT
import cats.implicits.catsSyntaxEitherId
import com.google.common.base.Charsets
import com.google.common.io.BaseEncoding
import com.typesafe.config.Config
import de.innfactory.bootstrapplay2.application.keycloak.domain.models.KeycloakUserCreation
import de.innfactory.bootstrapplay2.application.keycloak.infrastructure.KeycloakRepository
import de.innfactory.bootstrapplay2.users.domain.interfaces.UserRepository
import de.innfactory.bootstrapplay2.users.domain.models.{User, UserId}
import de.innfactory.bootstrapplay2.users.infrastructure.mappers.KeycloakUserMapper
import de.innfactory.play.controller.ResultStatus
import de.innfactory.play.results.Results.Result
import org.joda.time.DateTime

import java.security.SecureRandom
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class KeycloakUserRepository @Inject() (keycloakRepository: KeycloakRepository, config: Config)(implicit
    ec: ExecutionContext,
    system: ActorSystem
) {

  def getAllUsersAsSource: Future[Source[User, NotUsed]] =
    (for {
      users <- keycloakRepository.getUsers(max = Int.MaxValue)
    } yield Source.fromIterator(() => users.iterator).map(KeycloakUserMapper.keycloakUserToUser)).value
      .map(_.toOption.getOrElse(Source.fromIterator(() => Seq.empty[User].iterator)))

  def getUserByEmail(email: String): Future[Result[User]] = {
    for {
      record <- keycloakRepository.getUserByEmail(email)
    } yield KeycloakUserMapper.keycloakUserToUser(record)
  }.value

  def getUserById(userId: UserId)(implicit ec: ExecutionContext): Future[Result[User]] =
    getUser(userId)

  def getUser(userId: UserId): Future[Result[User]] = {
    for {
      record <- keycloakRepository.getUser(userId.value)
    } yield KeycloakUserMapper.keycloakUserToUser(record)
  }.value

  def createUser(email: String, firstName: String, lastName: String, password: Option[String]): Future[Result[User]] = {
    val userCreation = KeycloakUserCreation(
      username = email,
      firstName = firstName,
      lastName = lastName,
      email = email
    )
    val secureRandom = new SecureRandom()
    val random = secureRandom.nextInt(10000000) * DateTime.now.getMillis // Generate Random Number
    val securePassword =
      BaseEncoding.base64().encode(random.toString.getBytes(Charsets.UTF_8)) // Generate Random Password

    for {
      _ <- keycloakRepository.createUser(userCreation)
      _ <- keycloakRepository.setUserPassword(email, password.getOrElse(securePassword))
      record <- keycloakRepository.getUserByEmail(email)
    } yield KeycloakUserMapper.keycloakUserToUser(record)
  }.value

  def setUserPassword(userId: UserId, newPassword: String, email: String): Future[Result[User]] = {
    for {
      updatedUser <- keycloakRepository.setUserPassword(email, newPassword)
    } yield KeycloakUserMapper.keycloakUserToUser(updatedUser)
  }.value

  def setEmailVerifiedState(userId: UserId, state: Boolean): Future[Result[Boolean]] = {
    for {
      _ <- keycloakRepository.setUserEmailVerified(userId.value, state)
    } yield true
  }.value

  def setUserDisabledState(userId: UserId, state: Boolean): Future[Result[Boolean]] = {
    for {
      _ <- keycloakRepository.setUserDisabled(userId.value, state)
    } yield true
  }.value

  def setUserDisplayName(userId: UserId, firstName: Option[String], lastName: Option[String]): Future[Result[Boolean]] =
    keycloakRepository.setUserDisplayName(userId.value, firstName, lastName).value.map(_.map(_ => true))

  def upsertUser(user: User, oldUser: User): Future[Result[User]] = {
    for {
      _ <-
        if (!user.emailVerified.equals(oldUser.emailVerified))
          EitherT(setEmailVerifiedState(user.userId, user.emailVerified))
        else EitherT(Future(user.asRight[ResultStatus]))
      _ <-
        if (!user.disabled.equals(oldUser.disabled)) EitherT(setUserDisabledState(user.userId, user.disabled))
        else EitherT(Future(user.asRight[ResultStatus]))
      _ <-
        if (user.firstName != oldUser.firstName || user.lastName != oldUser.lastName)
          EitherT(setUserDisplayName(user.userId, firstName = user.firstName, lastName = user.lastName))
        else EitherT(Future(user.asRight[ResultStatus]))
    } yield ()
    getUser(user.userId)
  }

  def setEmailVerified(userId: UserId): Future[Result[Boolean]] = {
    for {
      _ <- keycloakRepository.setUserEmailVerified(userId.value, true)
    } yield true
  }.value
}
