package testutils.grapqhl

import de.innfactory.bootstrapplay2.models.api.Company
import play.api.libs.json.{ JsObject, Json }

object CompanyRequests {

  object CompanyRequest {
    private val body = Json.parse("""{"operationName":"Companies"}""")

    implicit val writesData                 = Json.reads[Data]
    implicit val writesCompanyRequestResult = Json.reads[CompanyRequestResult]

    case class Data(allCompanies: List[Company])

    case class CompanyRequestResult(data: Data)

    def getRequest(filter: Option[String]): JsObject = {
      val addition = if (filter.isDefined) "( filter: \"" + filter.get + "\")" else ""
      body.as[JsObject] ++ Json.obj(
        "query" ->
          s"""query Companies {
             |  allCompanies$addition {
             |    id
             |    firebaseUser
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
