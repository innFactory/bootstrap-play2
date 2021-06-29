package de.innfactory.bootstrapplay2.locations.application.models

import de.innfactory.bootstrapplay2.locations.domain.models.Location
import org.joda.time.DateTime
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import io.scalaland.chimney.dsl._
import io.scalaland.chimney._

case class LocationResponse(
  id: Long,
  company: Long,
  name: Option[String],
  settings: Option[JsValue],
  addressLine1: Option[String],
  addressLine2: Option[String],
  zip: Option[String],
  city: Option[String],
  country: Option[String],
  created: DateTime,
  updated: DateTime
)

object LocationResponse {
  implicit val format = Json.format[LocationResponse]

  def fromLocation(location: Location): LocationResponse =
    location
      .into[LocationResponse]
      .withFieldComputed[Long, Long](_.id, _.id.map(_.value).getOrElse(0))
      .withFieldComputed(_.company, _.company.value)
      .withFieldComputed(_.created, _.created.getOrElse(DateTime.now()))
      .withFieldComputed(_.updated, _.updated.getOrElse(DateTime.now()))
      .transform
}
