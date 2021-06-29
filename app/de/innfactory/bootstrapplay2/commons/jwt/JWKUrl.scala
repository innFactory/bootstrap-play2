package de.innfactory.bootstrapplay2.commons.jwt

import play.api.libs.json.Json

final case class JWKUrl(value: String) extends AnyVal

object JWKUrl {
  implicit val reads  = Json.reads[JWKUrl]
  implicit val writes = Json.writes[JWKUrl]
}
