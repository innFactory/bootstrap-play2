package de.innfactory.bootstrapplay2.locations.domain.models

import de.innfactory.bootstrapplay2.companies.domain.models.CompanyId
import de.innfactory.implicits.OptionUtils.EnhancedOption
import org.joda.time.DateTime
import play.api.libs.json._

case class Location(
    id: LocationId,
    company: CompanyId,
    name: Option[String],
    settings: Option[JsValue],
    addressLine1: Option[String],
    addressLine2: Option[String],
    zip: Option[String],
    city: Option[String],
    country: Option[String],
    created: DateTime,
    updated: DateTime
) {
  def patch(newObject: Location): Location =
    newObject.copy(
      id = this.id,
      company = this.company,
      name = newObject.name.getOrElseOld(this.name),
      addressLine1 = newObject.addressLine1.getOrElseOld(this.addressLine1),
      addressLine2 = newObject.addressLine2.getOrElseOld(this.addressLine2),
      zip = newObject.zip.getOrElseOld(this.zip),
      city = newObject.city.getOrElseOld(this.city),
      country = newObject.country.getOrElseOld(this.country),
      settings = newObject.settings.getOrElseOld(this.settings),
      created = this.created,
      updated = DateTime.now
    )
}
