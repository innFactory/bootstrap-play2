package de.innfactory.bootstrapplay2.application.keycloak.domain.models

import play.api.libs.json.{Json, OFormat}

case class KeycloakRoles(
    id: String,
    name: String,
    composite: Boolean,
    clientRole: Boolean,
    containerId: String
)

object KeycloakRoles {
  implicit val formatKeycloakRoles: OFormat[KeycloakRoles] = Json.format[KeycloakRoles]
}
