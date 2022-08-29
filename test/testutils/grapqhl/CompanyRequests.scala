package testutils.grapqhl

import de.innfactory.bootstrapplay2.apidefinition.CompanyResponse
import play.api.libs.json.{JsObject, Json}

object CompanyRequests {

  object CompanyRequest {
    private val body = Json.parse("""{"operationName":"Companies"}""")

    def getRequest(filter: Option[String]): JsObject = {
      val addition = if (filter.isDefined) "( filter: \"" + filter.get + "\")" else ""
      body.as[JsObject] ++ Json.obj(
        "query" ->
          s"""query Companies {
             |  allCompanies$addition {
             |    id
             |    settings
             |    stringAttribute1
             |    stringAttribute2
             |    longAttribute1
             |    booleanAttribute
             |    created
             |    updated
             |  }
             |}""".stripMargin
      )
    }
  }

}
