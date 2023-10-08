package de.innfactory.bootstrapplay2.users.infrastructure

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import com.google.common.base.Charsets
import com.google.common.io.BaseEncoding
import com.google.firebase.auth.{ExportedUserRecord, FirebaseAuth, FirebaseAuthException, UserRecord}
import de.innfactory.play.results.Results.Result
import de.innfactory.play.results.errors.Errors.{BadRequest, NotFound}
import org.joda.time.DateTime
import play.api.libs.json.{JsNull, _}
import cats.implicits._
import de.innfactory.bootstrapplay2.users.domain.models.{Claims, User, UserId}
import de.innfactory.bootstrapplay2.users.infrastructure.mappers.UserRecordMapper.userRecordToUser
import de.innfactory.bootstrapplay2.users.infrastructure.mappers.ClaimMapper.claimsToMap
import java.security.SecureRandom
import javax.inject.Inject
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.util.Try
import scala.util.control.NonFatal

class FirebaseUserRepository @Inject() (implicit ec: ExecutionContext, system: ActorSystem) {

  private val firebaseInstance = FirebaseAuth.getInstance()

  def getUsersSource: Source[ExportedUserRecord, NotUsed] =
    Source.fromIterator(() => firebaseInstance.listUsers(null).iterateAll().iterator().asScala)

  def getUserByEmail(email: String): Result[User] =
    for {
      record <- Try(firebaseInstance.getUserByEmail(email)).toEither.leftMap { case NonFatal(ex) =>
        NotFound(ex.getMessage)
      }
    } yield userRecordToUser(record)

  def getUser(userId: UserId): Result[User] =
    for {
      record <- Try(firebaseInstance.getUser(userId.value)).toEither.leftMap { case NonFatal(ex) =>
        NotFound(ex.getMessage)
      }
    } yield userRecordToUser(record)

  def createUser(email: String): Result[User] = {
    val createRequest = new UserRecord.CreateRequest()
    val secureRandom = new SecureRandom()
    val random = secureRandom.nextInt(10000000) * DateTime.now.getMillis // Generate Random Number
    val password = BaseEncoding.base64().encode(random.toString.getBytes(Charsets.UTF_8)) // Generate Random Password
    createRequest.setPassword(password) // set password
    createRequest.setEmail(email) // set email
    for {
      record <- Try(firebaseInstance.createUser(createRequest)).toEither.leftMap {
        case e: FirebaseAuthException =>
          BadRequest(e.getCause.getMessage.replaceFirst(e.getCause.getMessage.split('{').head, ""))
        case NonFatal(ex) =>
          NotFound(ex.getMessage)
      }
    } yield userRecordToUser(record)
  }

  def setUserClaims(userId: UserId, claims: Claims): Result[Boolean] = {
    val firebaseInstance = FirebaseAuth.getInstance()
    val userRecord = firebaseInstance.getUser(userId.value)
    val claimsMap = claimsToMap(claims)
    val parsedClaims = claimsMap.map { e =>
      (
        e._1,
        e._2 match {
          case JsNull => None
          case boolean: JsBoolean =>
            if (boolean.value)
              Some(java.lang.Boolean.TRUE)
            else
              Some(java.lang.Boolean.FALSE)
          case JsNumber(value) => Some(value.bigDecimal)
          case JsString(value) => Some(value)
          case JsArray(value)  => Some(value.map(_.toString().toInt).toSet.asJavaCollection)
          case JsObject(_)     => None
        }
      )
    }.filter(_._2.isDefined).map(e => e._1 -> e._2.get)
    firebaseInstance.setCustomUserClaims(userRecord.getUid, parsedClaims.asJava) // set User Claims to firebase
    Right(true)
  }

  def setUserPassword(userId: UserId, newPassword: String): Result[User] = {
    val firebaseInstance = FirebaseAuth.getInstance()
    val userRecord = firebaseInstance.getUser(userId.value)
    val updateRequest = userRecord.updateRequest()
    updateRequest.setPassword(newPassword)
    updateRequest.setEmailVerified(true)
    val updatedUser = firebaseInstance.updateUser(updateRequest)
    Right(userRecordToUser(updatedUser))
  }

  def setEmailVerifiedState(userId: UserId, state: Boolean): Result[Boolean] =
    try {
      val firebaseInstance = FirebaseAuth.getInstance()
      val userRecord = firebaseInstance.getUser(userId.value)
      val updateRequest = userRecord.updateRequest()
      updateRequest.setEmailVerified(state)
      firebaseInstance.updateUser(updateRequest)
      Right(true)
    } catch {
      case _: Throwable => Left(BadRequest())
    }

  def setUserDisabledState(userId: UserId, state: Boolean): Result[Boolean] =
    try {
      val firebaseInstance = FirebaseAuth.getInstance()
      val userRecord = firebaseInstance.getUser(userId.value)
      val updateRequest = userRecord.updateRequest()
      updateRequest.setDisabled(state)
      firebaseInstance.updateUser(updateRequest)
      Right(true)
    } catch {
      case _: Throwable => Left(BadRequest())
    }

  def setUserDisplayName(userId: UserId, displayName: Option[String]): Result[Boolean] = {
    val firebaseInstance = FirebaseAuth.getInstance()
    val userRecord = firebaseInstance.getUser(userId.value)
    val updateRequest = userRecord.updateRequest()
    updateRequest.setDisplayName(displayName.orNull)
    firebaseInstance.updateUser(updateRequest)
    Right(true)
  }

  def upsertUser(user: User, oldUser: User): Result[User] = {
    for {
      _ <-
        if (!user.emailVerified.equals(oldUser.emailVerified)) setEmailVerifiedState(user.userId, user.emailVerified)
        else Right(user)
      _ <-
        if (!user.disabled.equals(oldUser.disabled)) setUserDisabledState(user.userId, user.disabled) else Right(user)
      _ <- if (!user.claims.equals(oldUser.claims)) setUserClaims(user.userId, user.claims) else Right(user)
      _ <-
        if (!user.displayName.equals(oldUser.displayName)) setUserDisplayName(user.userId, user.displayName)
        else Right(user)
    } yield ()
    getUser(user.userId)
  }

  def setEmailVerified(userId: String): Result[Boolean] =
    try {
      val firebaseInstance = FirebaseAuth.getInstance()
      val userRecord = firebaseInstance.getUser(userId)
      val updateRequest = userRecord.updateRequest()
      updateRequest.setEmailVerified(true)
      firebaseInstance.updateUser(updateRequest)
      Right(true)
    } catch {
      case _: Throwable => Left(BadRequest())
    }
}
