package de.innfactory.bootstrapplay2.companies.domain.models

import de.innfactory.bootstrapplay2.commons.implicits.OptionUtils._
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class Company(
  id: Option[CompanyId],
  settings: Option[JsValue],
  stringAttribute1: Option[String],
  stringAttribute2: Option[String],
  longAttribute1: Option[Long],
  booleanAttribute: Option[Boolean],
  created: Option[DateTime],
  updated: Option[DateTime]
)

object Company {

  def patch(newObject: Company, oldObject: Company): Company =
    newObject.copy(
      id = oldObject.id,
      created = oldObject.created,
      updated = Some(DateTime.now)
    )

}
