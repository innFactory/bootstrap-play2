package de.innfactory.bootstrapplay2.services.firebase.models

case class UserPasswordResetTokens(
  userId: String,
  token: String,
  created: org.joda.time.DateTime,
  validUntil: org.joda.time.DateTime
)
