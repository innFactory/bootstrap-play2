package de.innfactory.bootstrapplay2.users.domain.models

import com.google.firebase.auth.UserRecord
import play.api.Logger
import play.api.libs.json.{ JsValue, Json }

case class User(
  userId: UserId,
  email: String,
  emailVerified: Boolean,
  disabled: Boolean,
  claims: Claims,
  displayName: Option[String],
  lastSignIn: Option[Long],
  lastRefresh: Option[Long],
  creation: Option[Long]
)

object User {}
