package testutils.grapqhl

import play.api.Application
import play.api.libs.json.JsObject
import play.api.mvc.{ Headers, Result }
import play.api.test.FakeRequest
import play.api.test.Helpers.{ route, POST }

import scala.concurrent.Future

object FakeGraphQLRequest {
  def getFake(body: JsObject, headers: (String, String)*)(implicit app: Application): FakeRequest[JsObject] =
    FakeRequest(POST, "/graphql")
      .withBody(body)
      .withHeaders(new Headers(headers))

  def routeResult(fakeRequest: FakeRequest[JsObject])(implicit app: Application): Future[Result] =
    route(app, fakeRequest).get
}
