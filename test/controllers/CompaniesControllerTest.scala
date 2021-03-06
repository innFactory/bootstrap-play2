package controllers

import java.util.UUID
import de.innfactory.bootstrapplay2.models.api._
import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json._
import play.api.test.Helpers._
import testutils.BaseFakeRequest
import testutils.BaseFakeRequest._

class CompaniesControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {

  /** ———————————————— */
  /** COMPANIES */
  /** ———————————————— */
  "CompaniesController" must {
    "get by id" in {
      val result = BaseFakeRequest(GET, "/v1/companies/231f5e3d-31db-4be5-9db9-92955e03507c")
        .withHeader(("Authorization", "test@test.de"))
        .get
        .parseContent[Company]
      result.firebaseUser.get.contains("test@test.de") mustEqual true
      result.id.get mustBe UUID.fromString("231f5e3d-31db-4be5-9db9-92955e03507c")
    }

    "get me" in {
      val result =
        BaseFakeRequest(GET, "/v1/companies/me").withHeader(("Authorization", "test@test.de")).get.parseContent[Company]
      result.firebaseUser.get.contains("test@test.de") mustEqual true
    }

    "get me empty" in {
      BaseFakeRequest(GET, "/v1/companies/me").withHeader(("Authorization", "test7@test7.de")).get checkStatus 403
    }

    "get single" in {
      val result =
        BaseFakeRequest(GET, "/v1/companies/231f5e3d-31db-4be5-9db9-92955e03507c")
          .withHeader(("Authorization", "test@test.de"))
          .get
          .parseContent[Company]
      result.firebaseUser.get.contains("test@test.de") mustEqual true
    }

    "get single forbidden" in {
      BaseFakeRequest(GET, "/v1/companies/231f5e3d-31db-4be5-9db9-92955e03507c")
        .withHeader(("Authorization", "test5@test5.de"))
        .get checkStatus 403
    }

    "post" in {
      val result =
        BaseFakeRequest(POST, "/v1/companies")
          .withHeader(("Authorization", "test5@test5.de"))
          .withJsonBody(Json.parse(s"""
                                      |{
                                      |"firebaseUser": [
                                      |"noUser@noUser.de"
                                      | ],
                                      |"settings": {
                                      |"test": "test"
                                      |},
                                      |"booleanAttribute": true,
                                      |"longAttribute1": 9
                                      |}
                                      |""".stripMargin))
          .getWithBody
          .parseContent[Company]
      result.firebaseUser.get.contains("noUser@noUser.de") mustEqual true
    }

    "post duplicate" in {
      BaseFakeRequest(POST, "/v1/companies")
        .withHeader(("Authorization", "test@test.de"))
        .withJsonBody(Json.parse(s"""
                                    |{
                                    |"id": "231f5e3d-31db-4be5-9db9-92955e03507c",
                                    |"firebaseUser": [
                                    |"test5@test5.de"
                                    | ],
                                    |"settings": {
                                    |"test": "test"
                                    |},
                                    |"booleanAttribute": true,
                                    |"longAttribute1": 9
                                    |}
                                    |""".stripMargin))
        .getWithBody checkStatus 400
    }

    "patch" in {
      val result =
        BaseFakeRequest(PATCH, "/v1/companies")
          .withHeader(("Authorization", "test@test.de"))
          .withJsonBody(Json.parse(s"""
                                     {
                                      | "id": "231f5e3d-31db-4be5-9db9-92955e03507c",
                                      | "firebaseUser": ["test@test.de"],
                                      | "settings": {"test2": "test2"},
                                      | "booleanAttribute": false,
                                      | "longAttribute1": 15
                                      |}
                                      |""".stripMargin))
          .getWithBody
          .parseContent[Company]
      result.firebaseUser.get.contains("test@test.de") mustEqual true
      result.settings.get
        .asInstanceOf[JsObject]
        .keys
        .find(x => x == "test2") mustBe Some(
        "test2"
      )
    }

    "delete" in {
      BaseFakeRequest(DELETE, "/v1/companies/b492fa98-ab60-4596-ac3c-256cc4957797")
        .withHeader(("Authorization", "test@test6.de"))
        .get checkStatus 204
      BaseFakeRequest(DELETE, "/v1/companies/b492fa98-ab60-4596-ac3c-256cc4957797")
        .withHeader(("Authorization", "test@test6.de"))
        .get checkStatus 403
    }

  }

}
