package testutils

import akka.util.Timeout
import org.scalatest
import play.api.Application
import play.api.libs.json.{JsValue, Reads}
import play.api.mvc.{AnyContentAsJson, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.scalatest.MustMatchers._

import scala.concurrent.Future

object BaseFakeRequest {

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
