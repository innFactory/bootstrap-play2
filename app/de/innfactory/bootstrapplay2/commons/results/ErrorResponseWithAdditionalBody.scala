package de.innfactory.bootstrapplay2.commons.results

import de.innfactory.play.controller.ErrorResponse
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ AnyContent, Request }

case class ErrorResponseWithAdditionalBody(message: String, details: JsValue) {
  def toJson = Json.toJson(this)(ErrorResponseWithAdditionalBody.writes)
}

object ErrorResponseWithAdditionalBody {

  implicit val reads  = Json.reads[ErrorResponseWithAdditionalBody]
  implicit val writes = Json.writes[ErrorResponseWithAdditionalBody]

  def fromRequest(message: String)(implicit request: Request[AnyContent]) =
    Json.toJson(ErrorResponse(message))

  def fromMessage(message: String) =
    Json.toJson(ErrorResponse(message))

}
