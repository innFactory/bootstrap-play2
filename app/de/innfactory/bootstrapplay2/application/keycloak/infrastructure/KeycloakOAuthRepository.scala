package de.innfactory.bootstrapplay2.application.keycloak.infrastructure

import com.typesafe.config.Config
import play.api.cache.SyncCacheApi
import play.api.libs.json.{Json, OFormat}
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcWSResponse

import javax.inject.Inject
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class KeycloakOAuthRepository @Inject() (wsClient: WSClient, syncCacheApi: SyncCacheApi, config: Config)(implicit
    ec: ExecutionContext
) {
  class InvalidKeyCloakAuth(message: String) extends Throwable

  case class Token(
      access_token: String,
      expires_in: Int
  )

  object Token {
    implicit val format: OFormat[Token] = Json.format[Token]
  }

  private val URL = config.getString("keycloak.url")
  private val BASE_PATH = config.getString("keycloak.basePath")
  private val CLIENT_ID = config.getString("keycloak.clientId")
  private val CLIENT_SECRET = config.getString("keycloak.clientSecret")
  private val REALM = config.getString("keycloak.realm")
  private val AUTH_REALM = config.getString("keycloak.authRealm")

  private val TOKEN_CACHE_KEY = "keycloak.token"

  def cachedToken(): Future[Token] = {
    val optionalToken = syncCacheApi.get[Token](TOKEN_CACHE_KEY)
    if (optionalToken.isEmpty) {
      getToken()
    } else {
      Future(optionalToken.get)
    }

  }

  def getToken(): Future[Token] =
    wsClient
      .url(s"$URL$BASE_PATH/realms/$AUTH_REALM/protocol/openid-connect/token")
      .addHttpHeaders("Content-Type" -> "application/x-www-form-urlencoded")
      .post(
        Map(
          "client_id" -> CLIENT_ID,
          "client_secret" -> CLIENT_SECRET,
          "grant_type" -> "client_credentials"
        )
      )
      .map { case AhcWSResponse(underlying) =>
        if (underlying.status == 200) {
          val token = Json.parse(underlying.body).as[Token]
          syncCacheApi.set(TOKEN_CACHE_KEY, token, token.expires_in.seconds)
          token
        } else {
          val errorMessage = s"Cannot resolve client token ${underlying.status} | ${underlying.body}"
          println(errorMessage)
          throw new InvalidKeyCloakAuth(errorMessage)
        }
      }

}
