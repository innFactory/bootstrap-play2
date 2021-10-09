package testutils.grapqhl

import de.innfactory.bootstrapplay2.companies.application.models.CompanyResponse
import play.api.libs.json.{ JsObject, Json }

object CompanyRequests {

  object CompanyRequest {
    private val body = Json.parse("""{"operationName":"Companies"}""")

    implicit val writesData                 = Json.format[Data]
    implicit val writesCompanyRequestResult = Json.format[CompanyRequestResult]

    case class Data(allCompanies: List[CompanyResponse])

    case class CompanyRequestResult(data: Data)

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
