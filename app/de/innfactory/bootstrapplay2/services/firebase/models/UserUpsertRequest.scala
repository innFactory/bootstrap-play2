package de.innfactory.bootstrapplay2.services.firebase.models

import de.innfactory.bootstrapplay2.services.firebase.claims.Claims.{ CompanyAdmin, InnFactoryAdmin }
import play.api.libs.json.{ JsValue, Json }

case class UserUpsertRequest(
  id: Option[String],
  email: String,
  disabled: Boolean,
  emailVerified: Boolean,
  claims: Claims,
  displayName: String
)

case class Claims(
  innFactoryAdmin: Option[Boolean],
  companyAdmin: Option[Long]
)

object Claims {
  implicit val writes = Json.writes[Claims]
  implicit val reads  = Json.reads[Claims]
}

object UserUpsertRequest {
  implicit val writes = Json.writes[UserUpsertRequest]
  implicit val reads  = Json.reads[UserUpsertRequest]

  implicit class EnhancedUserUpsertRequest(value: UserUpsertRequest) {

    def toUser: Option[User] = {

      // Parse Claims
      var claims = Map.empty[String, JsValue]
      if (value.claims.innFactoryAdmin.isDefined)
        claims = claims + (InnFactoryAdmin().key -> Json.toJson(value.claims.innFactoryAdmin.get))
      if (value.claims.companyAdmin.isDefined)
        claims = claims + (CompanyAdmin().key -> Json.toJson(value.claims.companyAdmin.get))

      if (value.id.isDefined)
        Some(
          User(
            id = value.id.get,
            email = value.email,
            emailVerified = value.emailVerified,
            disabled = value.disabled,
            claims = claims,
            displayName = value.displayName,
            lastSignIn = 0,
            lastRefresh = 0,
            creation = 0
          )
        )
      else None
    }
  }
}
