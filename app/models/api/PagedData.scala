package models.api

import play.api.libs.json.{JsValue, Json}

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
    implicit val pagedDataWriter = Json.writes[models.api.PagedData[JsValue]]
  implicit val pagedDataReader = Json.reads[models.api.PagedData[JsValue]]
    def prevGen(to: Int, from: Int, count: Int, apiString: String):String = {
      if(count == 0) {
        ""
      }else {
        val lowerFrom = from - (to - from) - 1
        val lowerTo = to - (to - from) - 1
        if (from > 0) {
          if(lowerFrom >= 0) {
            apiString.concat("?startIndex=".concat(lowerFrom.toString.concat("&endIndex=".concat(lowerTo.toString))))
          } else {
            apiString.concat("?startIndex=0&endIndex=".concat(lowerTo.toString))
          }
        } else {
          ""
        }
      }

    }
    def nextGen(to: Int, from: Int, count: Int, apiString: String)={
      if(count == 0) {
        ""
      } else {
        val upperTo = to + (to - from) + 1
        val upperFrom = from + (to - from) + 1
        val limit = count - 1
        if(upperTo <= limit) {
          apiString.concat("?startIndex=".concat(upperFrom.toString.concat("&endIndex=".concat(upperTo.toString))))
        }else {
          if(upperFrom > limit) {
            ""
          } else {
            apiString.concat("?startIndex=".concat(upperFrom.toString.concat("&endIndex=".concat(limit.toString))))
          }
        }
      }


    }
  }
