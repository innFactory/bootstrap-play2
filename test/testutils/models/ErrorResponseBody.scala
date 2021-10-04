package testutils.models

import play.api.libs.json.Json

case class ErrorResponseBody(message: String)

object ErrorResponseBody {
  implicit val format = Json.format[ErrorResponseBody]
}
