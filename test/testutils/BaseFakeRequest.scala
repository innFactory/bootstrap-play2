package testutils

import akka.util.Timeout
import org.scalatest
import play.api.Application
import play.api.libs.json.{ JsValue, Reads }
import play.api.mvc.{ AnyContentAsJson, Result }
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.scalatest.MustMatchers._

import scala.concurrent.Future

class BaseFakeRequest(method: String, url: String)(implicit app: Application) {

  private var fakeRequest = FakeRequest(method, url)

  private var fakeRequestWithBody: FakeRequest[AnyContentAsJson] = _

  def get =
    route(
      app,
      fakeRequest
    ).get

  def getWithBody =
    route(
      app,
      fakeRequestWithBody
    ).get

  def withHeader(header: (String, String)): BaseFakeRequest = {
    fakeRequest = fakeRequest.withHeaders(header)
    this
  }

  def withJsonBody(body: JsValue): BaseFakeRequest = {
    fakeRequestWithBody = fakeRequest.withJsonBody(body)
    this
  }

}

object BaseFakeRequest {

  def apply(method: String, url: String)(implicit app: Application) = new BaseFakeRequest(method, url)

  implicit class EnhancedResult(result: Future[Result]) {
    def parseContent[T](implicit reads: Reads[T]): T = {
      val content = contentAsJson(result)
      content.as[T]
    }

    def getStatus(implicit timeout: Timeout): Int =
      status(result)(timeout)

    def checkStatus(expectedStatus: Int): scalatest.Assertion =
      result.getStatus mustEqual expectedStatus
  }

}
