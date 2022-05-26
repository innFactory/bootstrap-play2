package de.innfactory.bootstrapplay2.users.infrastructure.mappers

import com.google.firebase.auth.UserRecord
import de.innfactory.bootstrapplay2.users.domain.models.{Claims, User, UserId}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}

import scala.collection.JavaConverters._

object UserRecordMapper {

  def userRecordToUser(userRecord: UserRecord): User = {
    val claimsMap = userRecord.getCustomClaims.asScala.toMap.map(k => k._1 -> getValueFromAnyRef(k._2))
    User(
      userId = UserId(userRecord.getUid),
      email = userRecord.getEmail,
      emailVerified = userRecord.isEmailVerified,
      disabled = userRecord.isDisabled,
      claims = Claims(
        innFactoryAdmin = claimsMap.get("innFactoryAdmin").map(v => v.as[Boolean]),
        companyAdmin = claimsMap.get("companyAdmin").map(v => v.as[Long])
      ),
      displayName = userRecord.getDisplayName match {
        case s: String => Some(s)
        case null      => None
      },
      lastSignIn = Some(userRecord.getUserMetadata.getLastSignInTimestamp),
      lastRefresh = Some(userRecord.getUserMetadata.getLastRefreshTimestamp),
      creation = Some(userRecord.getUserMetadata.getCreationTimestamp)
    )
  }

  def getValueFromAnyRef(a: AnyRef): JsValue =
    a match {
      case s: java.lang.String     => Json.toJson(s)
      case l: java.math.BigDecimal => Json.toJson(l.longValue)
      case b: java.lang.Boolean    => Json.toJson(b.booleanValue())
      case value =>
        Logger("play").logger.warn("Parsing unknown userClaims: " + value.getClass)
        Json.toJson("unknown")
    }

}
