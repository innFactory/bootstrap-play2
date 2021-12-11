package de.innfactory.bootstrapplay2.users.application.models

import play.api.libs.json.Json

case class UserPasswordResetRequest(
  userId: String,
  password: String,
  token: String
)

object UserPasswordResetRequest {
  implicit val writes = Json.writes[UserPasswordResetRequest]
  implicit val reads  = Json.reads[UserPasswordResetRequest]
}
