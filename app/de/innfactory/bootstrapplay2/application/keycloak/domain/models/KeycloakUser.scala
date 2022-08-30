package de.innfactory.bootstrapplay2.application.keycloak.domain.models

import play.api.libs.json.{Json, OFormat}

case class KeycloakUser(
    id: String,
    createdTimestamp: Long,
    username: String,
    enabled: Boolean,
    totp: Boolean,
    emailVerified: Boolean,
    firstName: Option[String],
    lastName: Option[String],
    email: Option[String],
    requiredActions: Seq[String],
    notBefore: Long
)

object KeycloakUser {
  implicit val formatKeycloakUser: OFormat[KeycloakUser] = Json.format[KeycloakUser]
}
