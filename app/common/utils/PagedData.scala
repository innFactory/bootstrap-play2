package common.utils

import play.api.libs.json.{ JsValue, Json }

case class PagedData[T](
  data: T,
  prev: String,
  next: String,
  count: Long
)

/**
 * Generator for Paging links
 * prevGen Takes from, to, count and the api endpoint end generates previous link
 * nextGen Takes from, to, count and the api endpoint end generates next link
 */
object PagedGen {
  implicit val pagedDataWriter = Json.writes[PagedData[JsValue]]
  implicit val pagedDataReader = Json.reads[PagedData[JsValue]]
  def prevGen(to: Int, from: Int, count: Int, apiString: String, query: Option[String]): String =
    if (count == 0) {
      ""
    } else {
      val lowerFrom = from - (to - from) - 1
      val lowerTo   = to - (to - from) - 1
      if (from > 0) {
        if (lowerFrom >= 0) {
          var api = apiString.concat(
            "?startIndex=".concat(lowerFrom.toString.concat("&endIndex=".concat(lowerTo.toString)))
          )
          if (query.isDefined) {
            api = api.concat(query.get)
          }
          api
        } else {
          var api =
            apiString.concat("?startIndex=0&endIndex=".concat(lowerTo.toString))
          if (query.isDefined) {
            api = api.concat(query.get)
          }
          api
        }
      } else {
        ""
      }
    }

  def nextGen(to: Int, from: Int, count: Int, apiString: String, query: Option[String]) =
    if (count == 0) {
      ""
    } else {
      val upperTo   = to + (to - from) + 1
      val upperFrom = from + (to - from) + 1
      val limit     = count - 1
      if (upperTo <= limit) {
        var api = apiString.concat(
          "?startIndex=".concat(upperFrom.toString.concat("&endIndex=".concat(upperTo.toString)))
        )
        if (query.isDefined) {
          api = api.concat(query.get)
        }
        api
      } else {
        if (upperFrom > limit) {
          ""
        } else {
          var api = apiString.concat(
            "?startIndex=".concat(upperFrom.toString.concat("&endIndex=".concat(limit.toString)))
          )
          if (query.isDefined) {
            api = api.concat(query.get)
          }
          api
        }
      }
    }

}
