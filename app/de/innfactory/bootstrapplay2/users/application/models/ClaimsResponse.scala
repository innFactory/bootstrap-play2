package de.innfactory.bootstrapplay2.users.application.models

import play.api.libs.json.Json

case class ClaimsResponse(
  innFactoryAdmin: Option[Boolean] = None,
  companyAdmin: Option[Boolean] = None
)

object ClaimsResponse {
  implicit val format = Json.format[ClaimsResponse]
}
