package de.innfactory.bootstrapplay2.companies.application.models

import de.innfactory.bootstrapplay2.companies.domain.models.{ Company, CompanyId }
import org.joda.time.DateTime
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import henkan.convert.Syntax._
import io.scalaland.chimney.dsl._
import io.scalaland.chimney._

case class CompanyResponse(
  id: Long,
  settings: Option[JsValue],
  stringAttribute1: Option[String],
  stringAttribute2: Option[String],
  longAttribute1: Option[Long],
  booleanAttribute: Option[Boolean],
  created: DateTime,
  updated: DateTime
)

object CompanyResponse {
  implicit val format = Json.format[CompanyResponse]

  def fromCompany(company: Company): CompanyResponse =
    company
      .into[CompanyResponse]
      .withFieldComputed[Long, Long](_.id, _.id.map(_.value).getOrElse(0))
      .withFieldComputed[DateTime, DateTime](_.created, c => c.created.getOrElse(DateTime.now()))
      .withFieldComputed[DateTime, DateTime](_.updated, c => c.updated.getOrElse(DateTime.now()))
      .transform

}
