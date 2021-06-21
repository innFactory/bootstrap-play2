package de.innfactory.bootstrapplay2.services.firebase.models

import com.google.firebase.auth.UserRecord
import play.api.Logger
import play.api.libs.json._

import scala.collection.JavaConverters._

case class User(
  id: String,
  email: String,
  emailVerified: Boolean,
  disabled: Boolean,
  claims: Map[String, JsValue],
  displayName: String,
  lastSignIn: Long,
  lastRefresh: Long,
  creation: Long
)

object User {
  implicit val reads  = Json.reads[User]
  implicit val writes = Json.writes[User]

  def getValueFromAnyRef(a: AnyRef): JsValue =
    a match {
      case s: java.lang.String     => Json.toJson(s)
      case l: java.math.BigDecimal => Json.toJson(l.longValue)
      case b: java.lang.Boolean    => Json.toJson(b.booleanValue())
      case value                   =>
        Logger("play").logger.warn("Parsing unknown userClaims: " + value.getClass)
        Json.toJson("unknown")
    }

  def fromUserRecord(userRecord: UserRecord): User =
    User(
      id = userRecord.getUid,
      email = userRecord.getEmail,
      emailVerified = userRecord.isEmailVerified,
      disabled = userRecord.isDisabled,
      claims = userRecord.getCustomClaims.asScala.toMap.map(k => (k._1 -> getValueFromAnyRef(k._2))),
      displayName = userRecord.getDisplayName match {
        case s: String => s
        case null      => ""
      },
      lastSignIn = userRecord.getUserMetadata.getLastSignInTimestamp,
      lastRefresh = userRecord.getUserMetadata.getLastRefreshTimestamp,
      creation = userRecord.getUserMetadata.getCreationTimestamp
    )

  def fromFirebaseUser(firebaseUser: FirebaseUser): User =
    User(
      id = firebaseUser.uid,
      email = firebaseUser.email,
      emailVerified = firebaseUser.emailVerified,
      disabled = firebaseUser.disabled,
      claims = firebaseUser.getCustomClaims.asScala.toMap.map(k => (k._1 -> Json.parse(k._2.toString))),
      displayName = firebaseUser.displayName,
      lastSignIn = firebaseUser.lastSignIn,
      lastRefresh = firebaseUser.lastRefresh,
      creation = firebaseUser.creation
    )

}
