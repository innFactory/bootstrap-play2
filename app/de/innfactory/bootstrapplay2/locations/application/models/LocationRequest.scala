package de.innfactory.bootstrapplay2.locations.application.models

import de.innfactory.bootstrapplay2.locations.domain.models.{ Location, LocationCompanyId, LocationId }
import org.joda.time.DateTime
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import henkan.convert.Syntax._
import io.scalaland.chimney.dsl._
import io.scalaland.chimney._

case class LocationRequest(
  id: Option[Long],
  company: Long,
  name: Option[String],
  settings: Option[JsValue],
  addressLine1: Option[String],
  addressLine2: Option[String],
  zip: Option[String],
  city: Option[String],
  country: Option[String]
) {
  def toLocation(): Location = this
    .into[Location]
    .withFieldComputed(_.id, l => l.id.map(LocationId))
    .withFieldComputed(_.company, l => LocationCompanyId(l.company))
    .withFieldComputed(_.created, _ => None)
    .withFieldComputed(_.updated, _ => None)
    .transform
}

object LocationRequest {
  implicit val format = Json.format[LocationRequest]
}
