package de.innfactory.bootstrapplay2.users.application.models

import de.innfactory.bootstrapplay2.users.domain.models.Claims

case class UserRequest(
  userId: String,
  email: String,
  emailVerified: Boolean,
  disabled: Boolean,
  claims: Claims,
  displayName: Option[String],
  lastSignIn: Option[Long],
  lastRefresh: Option[Long],
  creation: Option[Long]
) {}
