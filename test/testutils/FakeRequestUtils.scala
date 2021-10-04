package testutils

import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.libs.json.{ Json, Writes }
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

object FakeRequestUtils extends PlaySpec {

  implicit class EnhancedFutureResult(result: Future[Result]) {
    def getStatus: Int = status(result)
  }

  def Post[T](url: String, body: T, authHeader: String)(implicit
    app: Application,
    writes: Writes[T]
  ): Future[Result] = {
    val future: Future[Result] =
      route(
        app,
        FakeRequest(POST, url)
          .withHeaders(
            ("Authorization", authHeader)
          )
          .withBody(Json.toJson(body))
      ).get
    future
  }

  def PostNoBody(url: String, authHeader: String)(implicit
    app: Application
  ): Future[Result] = {
    val future: Future[Result] =
      route(
        app,
        FakeRequest(POST, url)
          .withHeaders(
            ("Authorization", authHeader)
          )
      ).get
    future
  }

  def Patch[T](url: String, body: T, authHeader: String)(implicit
    app: Application,
    writes: Writes[T]
  ): Future[Result] = {
    val future: Future[Result] =
      route(
        app,
        FakeRequest(PATCH, url)
          .withHeaders(
            ("Authorization", authHeader)
          )
          .withBody(Json.toJson(body))
      ).get
    future
  }

  def PatchNoBody[T](url: String, authHeader: String)(implicit
    app: Application
  ): Future[Result] = {
    val future: Future[Result] =
      route(
        app,
        FakeRequest(PATCH, url)
          .withHeaders(
            ("Authorization", authHeader)
          )
      ).get
    future
  }

  def Get[T](url: String, authHeader: String)(implicit app: Application): Future[Result] = {
    val future: Future[Result] =
      route(
        app,
        FakeRequest(GET, url)
          .withHeaders(
            ("Authorization", authHeader)
          )
      ).get
    future
  }

  def Delete[T](url: String, authHeader: String)(implicit app: Application): Future[Result] = {
    val future: Future[Result] =
      route(
        app,
        FakeRequest(DELETE, url)
          .withHeaders(
            ("Authorization", authHeader)
          )
      ).get
    future
  }

  def DeleteWithBody[T](url: String, body: T, authHeader: String)(implicit
    app: Application,
    writes: Writes[T]
  ): Future[Result] = {
    val future: Future[Result] =
      route(
        app,
        FakeRequest(DELETE, url)
          .withHeaders(
            ("Authorization", authHeader)
          )
          .withBody(Json.toJson(body))
      ).get
    future
  }
}
