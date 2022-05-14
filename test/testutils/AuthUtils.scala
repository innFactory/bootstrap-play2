package testutils

import com.typesafe.config.Config
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import javax.inject.Inject
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class AuthUtils @Inject(wsClient: WSClient, config: Config) {
  import AuthUtils._

  def NotVerifiedEmailToken: String = getTokenForEmail(NotVerifiedEmail)
  def CompanyAdminEmailToken: String = getTokenForEmail(CompanyAdminEmail)

  def getTokenFor(email: String) = getTokenForEmail(email)

  private def getTokenForEmail(email: String): String = {
    val result = wsClient
      .url("http://localhost:9099/identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=default")
      .post(
        Json.parse(
          s"""
             |{
             |    "email": "$email",
             |    "password": "testtest",
             |    "returnSecureToken":true
             |}
             |""".stripMargin
        )
      )
    val res = Await.result(result, 5.seconds)
    val token = res.json.\("idToken").as[String]
    token
  }

}

object AuthUtils {
  val NotVerifiedEmail = "notverified@innfactory.de"
  val NotVerifiedEmailId = "WasFqkZRc9TktJmu2DoDSFienga2"
  val CompanyAdminEmail = "test@test.de"
  val CompanyAdminEmailId = "W88FqkZRc9TktJmu2Dow4xUkT0UU"
}
