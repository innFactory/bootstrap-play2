package de.innfactory.bootstrapplay2.locations.domain.models

import de.innfactory.bootstrapplay2.commons.implicits.OptionUtils._
import org.joda.time.DateTime
import play.api.libs.json._

case class Location(
  id: Option[LocationId],
  company: LocationCompanyId,
  name: Option[String],
  settings: Option[JsValue],
  addressLine1: Option[String],
  addressLine2: Option[String],
  zip: Option[String],
  city: Option[String],
  country: Option[String],
  created: Option[DateTime],
  updated: Option[DateTime]
)

object Location {

  def patch(newObject: Location, oldObject: Location): Location =
    newObject.copy(
      id = oldObject.id,
      company = oldObject.company,
      name = newObject.name.getOrElseOld(oldObject.name),
      addressLine1 = newObject.addressLine1.getOrElseOld(oldObject.addressLine1),
      addressLine2 = newObject.addressLine2.getOrElseOld(oldObject.addressLine2),
      zip = newObject.zip.getOrElseOld(oldObject.zip),
      city = newObject.city.getOrElseOld(oldObject.city),
      country = newObject.country.getOrElseOld(oldObject.country),
      settings = newObject.settings.getOrElseOld(oldObject.settings),
      created = oldObject.created,
      updated = Some(DateTime.now)
    )

}
