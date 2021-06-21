package de.innfactory.bootstrapplay2.services.firebase.models

import play.api.libs.json.{ JsValue, Json }

case class UserPasswordResetRequest(
  userId: String,
  password: String,
  token: String
)

object UserPasswordResetRequest {
  implicit val writes = Json.writes[UserPasswordResetRequest]
  implicit val reads  = Json.reads[UserPasswordResetRequest]
}
