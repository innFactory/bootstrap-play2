package de.innfactory.bootstrapplay2.users.infrastructure.mappers

import de.innfactory.bootstrapplay2.application.keycloak.domain.models.KeycloakUser
import de.innfactory.bootstrapplay2.users.domain.models.{Claims, User, UserId}

object KeycloakUserMapper {
  def keycloakUserToUser(keycloakUser: KeycloakUser): User =
    User(
      userId = UserId(keycloakUser.id),
      email = keycloakUser.email.getOrElse(""),
      emailVerified = keycloakUser.emailVerified,
      disabled = !keycloakUser.enabled,
      firstName = keycloakUser.firstName,
      lastName = keycloakUser.lastName,
      displayName = (keycloakUser.firstName, keycloakUser.lastName) match {
        case (Some(firstName), Some(lastName)) => Some(s"$firstName $lastName")
        case (Some(firstName), None)           => Some(firstName)
        case (None, Some(lastName))            => Some(lastName)
        case _                                 => None
      },
      lastSignIn = None,
      lastRefresh = None,
      creation = Some(keycloakUser.createdTimestamp),
      claims = Claims()
    )
}
