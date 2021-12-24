package de.innfactory.bootstrapplay2.companies.application.models

import de.innfactory.bootstrapplay2.companies.domain.models.{Company, CompanyId}
import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import io.scalaland.chimney.dsl._

case class CompanyRequest(
    id: Option[Long],
    settings: Option[JsValue],
    stringAttribute1: Option[String],
    stringAttribute2: Option[String],
    longAttribute1: Option[Long],
    booleanAttribute: Option[Boolean]
) {

  def toCompany(): Company = this
    .into[Company]
    .withFieldComputed(_.id, _.id.map(CompanyId))
    .withFieldComputed(_.created, _ => None)
    .withFieldComputed(_.updated, _ => None)
    .transform

}

object CompanyRequest {
  implicit val reads = Json.reads[CompanyRequest]
}
