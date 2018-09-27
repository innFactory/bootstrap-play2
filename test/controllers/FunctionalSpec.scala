package controllers

import models.api._
import org.scalatestplus.play.{BaseOneAppPerSuite, PlaySpec}
import play.api.libs.json._
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.libs.json.Reads
import models.api.PagedGen._
import scala.concurrent.Future

/**
 * Runs a functional test with the application, using an in memory
 * database.  Migrations are handled automatically by play-flyway
 */
class FunctionalSpec extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {

  implicit val nilReader = Json.reads[scala.collection.immutable.Nil.type]
  implicit val nilWriter = Json.writes[scala.collection.immutable.Nil.type]
  implicit val contactdReader = Json.reads[Contact]
  implicit val contactdWriter = Json.writes[Contact]
  implicit val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

  "App" should {
    "work with postgres Database" in {
      val future = route(app, FakeRequest(GET, "/v1/contact")).get
      status(future) mustEqual 200
    }
  }

  "PagedGen" should {
    "generate correct prev and next links" in {
      val prevLink = PagedGen.prevGen(8, 5, 12, "test")
      val prevZeroLink = PagedGen.prevGen(8, 5, 0, "test")
      val prevOneLink = PagedGen.prevGen(8, 3, 12, "test")
      val prevTwoLink = PagedGen.prevGen(0, 0, 12, "test")
      val nextLink = PagedGen.nextGen(8, 5, 12, "test")
      val nextZeroLink = PagedGen.nextGen(8, 5, 0, "test")
      val nextOneLink = PagedGen.nextGen(10, 5, 12, "test")
      val nextTwoLink = PagedGen.nextGen(11, 11, 12, "test")
      prevLink mustEqual "test?startIndex=1&endIndex=4"
      prevZeroLink mustEqual ""
      prevOneLink mustEqual "test?startIndex=0&endIndex=2"
      prevTwoLink mustEqual ""
      nextLink mustEqual "test?startIndex=9&endIndex=11"
      nextZeroLink mustEqual ""
      nextOneLink mustEqual "test?startIndex=11&endIndex=11"
      nextTwoLink mustEqual ""
    }
  }

}
