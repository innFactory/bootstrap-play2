package de.innfactory.bootstrapplay2.services.firebase

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import com.google.common.base.Charsets
import com.google.common.io.BaseEncoding
import com.google.firebase.auth.{ ExportedUserRecord, FirebaseAuth, FirebaseAuthException, UserRecord }
import de.innfactory.bootstrapplay2.common.results.Results.Result
import de.innfactory.bootstrapplay2.common.results.errors.Errors.{ BadRequest, NotFound }
import de.innfactory.bootstrapplay2.services.firebase.models.{ FirebaseUser, User }
import de.innfactory.bootstrapplay2.services.firebase.utils.Utils.EnhancedUserRecord
import org.joda.time.DateTime
import play.api.libs.json.{ JsNull, JsValue, _ }

import javax.inject.Inject
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

class FirebaseUserService @Inject() (implicit ec: ExecutionContext, system: ActorSystem) {

  private val firebaseInstance = FirebaseAuth.getInstance()

  def getUsersSource: Source[ExportedUserRecord, NotUsed] =
    Source.fromIterator(() => firebaseInstance.listUsers(null).iterateAll().iterator().asScala)

  def getUserByEmail(email: String): Result[User] =
    try {
      val userRecord: UserRecord = firebaseInstance.getUserByEmail(email) // Query User
      Right(userRecord.toUser)
    } catch {
      case _: Throwable => Left(NotFound())
    }

  def _internal_getUser(userId: String): FirebaseUser =
    firebaseInstance.getUser(userId).toFirebaseUser

  def getUser(userId: String): Result[User] =
    try {
      val userRecord: UserRecord = firebaseInstance.getUser(userId) // Query User
      Right(userRecord.toUser)
    } catch {
      case _: Throwable => Left(NotFound())
    }

  def createUser(email: String): Result[User] = {
    val createRequest = new UserRecord.CreateRequest()
    val random        = (scala.util.Random.nextInt(10000000) * DateTime.now.getMillis)         // Generate Random Number
    val password      = BaseEncoding.base64().encode(random.toString.getBytes(Charsets.UTF_8)) // Generate Random Password
    createRequest.setPassword(password) // set password
    createRequest.setEmail(email)       // set email
    try {
      val firebaseRecord = firebaseInstance.createUser(createRequest) // Create User
      Right(firebaseRecord.toUser)
    } catch {
      case e: FirebaseAuthException =>
        Left(BadRequest(e.getCause.getMessage.replaceFirst(e.getCause.getMessage.split('{').head, "")))
    }
  }

  def setUserClaims(userId: String, claims: Map[String, JsValue]): Result[Boolean] = {
    val firebaseInstance = FirebaseAuth.getInstance()
    val userRecord       = firebaseInstance.getUser(userId)
    val parsedClaims     = claims.map { e =>
      (
        e._1,
        e._2 match {
          case JsNull             => None
          case boolean: JsBoolean =>
            if (boolean.value)
              Some(java.lang.Boolean.TRUE)
            else
              Some(java.lang.Boolean.FALSE)
          case JsNumber(value)    => Some(value.bigDecimal)
          case JsString(value)    => Some(value)
          case JsArray(value)     => Some(value.map(_.toString().toInt).toSet.asJavaCollection)
          case JsObject(_)        => None
        }
      )
    }.filter(_._2.isDefined).map(e => (e._1 -> e._2.get))
    firebaseInstance.setCustomUserClaims(userRecord.getUid, parsedClaims.asJava) // set User Claims to firebase
    Right(true)
  }

  def setUserPassword(userId: String, newPassword: String): Result[User] = {
    val firebaseInstance = FirebaseAuth.getInstance()
    val userRecord       = firebaseInstance.getUser(userId)
    val updateRequest    = userRecord.updateRequest()
    updateRequest.setPassword(newPassword)
    updateRequest.setEmailVerified(true)
    val updatedUser      = firebaseInstance.updateUser(updateRequest)
    Right(updatedUser.toUser)
  }

  def setEmailVerifiedState(userId: String, state: Boolean): Result[Boolean] =
    try {
      val firebaseInstance = FirebaseAuth.getInstance()
      val userRecord       = firebaseInstance.getUser(userId)
      val updateRequest    = userRecord.updateRequest()
      updateRequest.setEmailVerified(state)
      firebaseInstance.updateUser(updateRequest)
      Right(true)
    } catch {
      case _: Throwable => Left(BadRequest())
    }

  def setUserDisabledState(userId: String, state: Boolean): Result[Boolean] =
    try {
      val firebaseInstance = FirebaseAuth.getInstance()
      val userRecord       = firebaseInstance.getUser(userId)
      val updateRequest    = userRecord.updateRequest()
      updateRequest.setDisabled(state)
      firebaseInstance.updateUser(updateRequest)
      Right(true)
    } catch {
      case _: Throwable => Left(BadRequest())
    }

  def setUserDisplayName(userId: String, displayName: String): Result[Boolean] = {
    val firebaseInstance = FirebaseAuth.getInstance()
    val userRecord       = firebaseInstance.getUser(userId)
    val updateRequest    = userRecord.updateRequest()
    updateRequest.setDisplayName(displayName)
    firebaseInstance.updateUser(updateRequest)
    Right(true)
  }

  def upsertUser(user: User, oldUser: User): Result[User] = {
    for {
      _ <- if (!user.emailVerified.equals(oldUser.emailVerified)) setEmailVerifiedState(user.id, user.emailVerified)
           else Right(user)
      _ <- if (!user.disabled.equals(oldUser.disabled)) setUserDisabledState(user.id, user.disabled) else Right(user)
      _ <- if (!user.claims.equals(oldUser.claims)) setUserClaims(user.id, user.claims) else Right(user)
      _ <- if (!user.displayName.equals(oldUser.displayName)) setUserDisplayName(user.id, user.displayName)
           else Right(user)
    } yield ()
    getUser(user.id)
  }

  def setEmailVerified(userId: String): Result[Boolean] =
    try {
      val firebaseInstance = FirebaseAuth.getInstance()
      val userRecord       = firebaseInstance.getUser(userId)
      val updateRequest    = userRecord.updateRequest()
      updateRequest.setEmailVerified(true)
      firebaseInstance.updateUser(updateRequest)
      Right(true)
    } catch {
      case _: Throwable => Left(BadRequest())
    }
}
