package de.innfactory.bootstrapplay2.users.domain.models

case class Claims(
  innFactoryAdmin: Option[Boolean] = None,
  companyAdmin: Option[Long] = None
)

object Claims {}
