package de.innfactory.bootstrapplay2.models.api

import java.util.UUID
import org.joda.time.DateTime
import play.api.libs.json._
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.libs.json.Reads
import de.innfactory.bootstrapplay2.common.utils.OptionUtils._

/**
 * Implementation independent aggregate root.
 */
case class Company(
  id: Option[UUID],
  firebaseUser: Option[List[String]],
  settings: Option[JsValue],
  stringAttribute1: Option[String],
  stringAttribute2: Option[String],
  longAttribute1: Long,
  booleanAttribute: Boolean,
  created: Option[DateTime],
  updated: Option[DateTime]
) extends ApiBaseModel {
  override def toJson: JsValue = Json.toJson(this)(Company.writes)
}

object Company {
  implicit val reads  = Json.reads[Company]
  implicit val writes = Json.writes[Company]

  def patch(newObject: Company, oldObject: Company): Company =
    newObject.copy(
      id = oldObject.id,
      settings = newObject.settings.getOrElseOld(oldObject.settings),
      firebaseUser = newObject.firebaseUser.getOrElseOld(oldObject.firebaseUser),
      created = oldObject.created,
      stringAttribute1 = newObject.stringAttribute1,
      stringAttribute2 = newObject.stringAttribute2,
      longAttribute1 = newObject.longAttribute1,
      booleanAttribute = newObject.booleanAttribute,
      updated = Some(DateTime.now)
    )

}
