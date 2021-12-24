package de.innfactory.bootstrapplay2.users.application.models

import de.innfactory.bootstrapplay2.users.domain.models.{UserId, UserPasswordResetToken}
import org.joda.time.DateTime
import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import io.scalaland.chimney.dsl._

case class UserPasswordResetTokenResponse(
    userId: String,
    token: String,
    created: DateTime,
    validUntil: DateTime
)

object UserPasswordResetTokenResponse {
  implicit val format = Json.format[UserPasswordResetTokenResponse]

  def fromUserPasswordResetToken(userPasswordResetToken: UserPasswordResetToken): UserPasswordResetTokenResponse =
    userPasswordResetToken
      .into[UserPasswordResetTokenResponse]
      .withFieldComputed(_.userId, u => u.userId.value)
      .transform
}
