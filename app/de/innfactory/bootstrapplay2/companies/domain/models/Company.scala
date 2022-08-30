package de.innfactory.bootstrapplay2.companies.domain.models

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class Company(
    id: CompanyId,
    settings: Option[JsValue],
    stringAttribute1: Option[String],
    stringAttribute2: Option[String],
    longAttribute1: Option[Long],
    booleanAttribute: Option[Boolean],
    created: DateTime,
    updated: DateTime
) {
  def patch(newObject: Company): Company =
    newObject.copy(
      id = this.id,
      created = this.created,
      updated = DateTime.now
    )
}
