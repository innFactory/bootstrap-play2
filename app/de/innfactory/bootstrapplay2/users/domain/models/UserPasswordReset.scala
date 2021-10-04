package de.innfactory.bootstrapplay2.users.domain.models

case class UserPasswordReset(
  userId: UserId,
  password: String,
  token: String
)
