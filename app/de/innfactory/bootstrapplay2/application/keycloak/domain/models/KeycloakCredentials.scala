package de.innfactory.bootstrapplay2.application.keycloak.domain.models

import play.api.libs.json.{Json, OFormat}

case class KeycloakCredentials(
    value: String,
    `type`: String,
    temporary: Boolean
)

object KeycloakCredentials {
  implicit val formatCredentials: OFormat[KeycloakCredentials] = Json.format[KeycloakCredentials]
}
