package de.innfactory.bootstrapplay2.application.keycloak.domain.models

import play.api.libs.json.{Json, OFormat}

case class KeycloakUserCreation(
    username: String,
    enabled: Boolean = true,
    totp: Boolean = false,
    emailVerified: Boolean = false,
    firstName: String,
    lastName: String,
    email: String
)

object KeycloakUserCreation {
  implicit val formatKeycloakUserCreation: OFormat[KeycloakUserCreation] = Json.format[KeycloakUserCreation]
}
