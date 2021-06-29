package de.innfactory.bootstrapplay2.commons.results

import play.api.libs.json.Json
import play.api.mvc.{ AnyContent, Request }

import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ AnyContent, Request }

case class ErrorResponse(message: String) {
  def toJson = Json.toJson(this)(ErrorResponse.writes)
}

object ErrorResponse {

  implicit val reads  = Json.reads[ErrorResponse]
  implicit val writes = Json.writes[ErrorResponse]

  def fromRequest(message: String)(implicit request: Request[AnyContent]) =
    Json.toJson(ErrorResponse(message))

  def fromMessage(message: String) =
    Json.toJson(ErrorResponse(message))

}
