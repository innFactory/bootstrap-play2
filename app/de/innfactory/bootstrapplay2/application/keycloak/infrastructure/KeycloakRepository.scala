package de.innfactory.bootstrapplay2.application.keycloak.infrastructure

import cats.data.EitherT
import com.typesafe.config.Config
import de.innfactory.bootstrapplay2.application.keycloak.domain.models.{
  KeycloakCredentials,
  KeycloakRoles,
  KeycloakUser,
  KeycloakUserCreation
}
import de.innfactory.play.common.ValidT
import de.innfactory.play.controller.ResultStatus
import de.innfactory.play.results.errors.Errors.{BadRequest, NotFound}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcWSResponse

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class KeycloakRepository @Inject() (
    wsClient: WSClient,
    keyCloakOAuthRepository: KeycloakOAuthRepository,
    config: Config
)(implicit
    ec: ExecutionContext
) {
  private val URL = config.getString("keycloak.url")
  private val BASE_PATH = config.getString("keycloak.basePath")
  private val CLIENT_ID = config.getString("keycloak.clientId")
  private val CLIENT_SECRET = config.getString("keycloak.clientSecret")
  private val REALM = config.getString("keycloak.realm")
  private val AUTH_REALM = config.getString("keycloak.authRealm")

  def get(url: String, expectedStatusCode: Int = 200): Future[Either[ResultStatus, JsValue]] =
    for {
      tokenResult <- keyCloakOAuthRepository.cachedToken()
      result <- wsClient
        .url(url)
        .withHttpHeaders("authorization" -> s"Bearer ${tokenResult.access_token}")
        .get()
        .map { case AhcWSResponse(underlying) =>
          if (underlying.status == expectedStatusCode) {
            // println(underlying.body)
            Right(Json.parse(underlying.body))
          } else Left(BadRequest(s"Could not get from $url | status code = ${underlying.status}"))
        }
    } yield result

  def put(url: String, body: JsValue, expectedStatusCode: Int = 200): Future[Either[ResultStatus, String]] =
    for {
      tokenResult <- keyCloakOAuthRepository.cachedToken()
      result <- wsClient
        .url(url)
        .withHttpHeaders("authorization" -> s"Bearer ${tokenResult.access_token}")
        .put(body)
        .map { case AhcWSResponse(underlying) =>
          if (underlying.status == expectedStatusCode) Right(underlying.body)
          else Left(BadRequest(s"Could not put from $url | status code = ${underlying.status}"))
        }
    } yield result

  def post(url: String, body: JsValue, expectedStatusCode: Int = 200): Future[Either[BadRequest, String]] =
    for {
      tokenResult <- keyCloakOAuthRepository.cachedToken()
      result <- wsClient
        .url(url)
        .withHttpHeaders("authorization" -> s"Bearer ${tokenResult.access_token}")
        .post(body)
        .map { case AhcWSResponse(underlying) =>
          // println(underlying.body)
          if (underlying.status == expectedStatusCode) Right(underlying.body)
          else Left(BadRequest(s"Could not post from $url | status code = ${underlying.status}"))
        }
    } yield result

  def validateToken(token: String): EitherT[Future, BadRequest, JsValue] =
    for {
      result <- EitherT(
        wsClient
          .url(s"$URL$BASE_PATH/realms/$REALM/protocol/openid-connect/userinfo")
          .withHttpHeaders("authorization" -> s"$token")
          .get()
          .map { case AhcWSResponse(underlying) =>
            // println(underlying.body)
            if (underlying.status == 200) {
              // println("Valid Token")
              Right(Json.parse(underlying.body))
            } else {
              println("Invalid Token")
              Left(BadRequest(s"Could not validate token"))
            }

          }
      )
    } yield result

  def getUsers(max: Int): EitherT[Future, ResultStatus, Seq[KeycloakUser]] =
    for {
      result <- EitherT(
        get(
          s"$URL$BASE_PATH/admin/realms/$REALM/users?max=$max"
        )
      )
    } yield result.as[Seq[KeycloakUser]]

  def getUser(userId: String): EitherT[Future, ResultStatus, KeycloakUser] =
    for {
      result <- EitherT(
        get(
          s"$URL$BASE_PATH/admin/realms/$REALM/users/$userId"
        )
      )
    } yield result.as[KeycloakUser]

  def createUser(user: KeycloakUserCreation): EitherT[Future, BadRequest, String] =
    for {
      result <- EitherT(
        post(
          s"$URL$BASE_PATH/admin/realms/$REALM/users",
          Json.toJson(user),
          expectedStatusCode = 201
        )
      )
    } yield result

  def getUserByEmail(email: String): EitherT[Future, ResultStatus, KeycloakUser] =
    for {
      result <- EitherT(
        get(
          s"$URL$BASE_PATH/admin/realms/$REALM/users?email=$email&exact=true"
        )
      )
      users = result.as[Seq[KeycloakUser]]
      _ <- ValidT(users.length == 1, NotFound(""))
    } yield users.head

  def getUserRoles(userId: String): EitherT[Future, ResultStatus, Seq[KeycloakRoles]] =
    for {
      result <- EitherT(
        get(
          s"$URL$BASE_PATH/admin/realms/$REALM/users/$userId/role-mappings/realm/composite"
        )
      )
    } yield result.as[Seq[KeycloakRoles]]

  def setUserEmailVerified(userId: String, value: Boolean): EitherT[Future, ResultStatus, Option[KeycloakUser]] =
    for {
      result <- getUser(userId)
      updated <- updateUser(result.copy(emailVerified = value))
    } yield updated

  def setUserPassword(email: String, password: String): EitherT[Future, ResultStatus, KeycloakUser] =
    for {
      result <- getUserByEmail(email)
      _ <- EitherT(
        put(
          s"$URL$BASE_PATH/admin/realms/$REALM/users/${result.id}/reset-password",
          Json.toJson(
            KeycloakCredentials(
              value = password,
              `type` = "awPassword",
              temporary = false
            )
          ),
          expectedStatusCode = 204
        )
      )
    } yield result

  def setUserDisabled(userId: String, value: Boolean): EitherT[Future, ResultStatus, Option[KeycloakUser]] =
    for {
      result <- getUser(userId)
      updated <- updateUser(result.copy(enabled = !value))
    } yield updated

  def setUserDisplayName(
      userId: String,
      firstName: Option[String],
      lastName: Option[String]
  ): EitherT[Future, ResultStatus, Option[KeycloakUser]] =
    for {
      result <- getUser(userId)
      updated <- updateUser(result.copy(firstName = firstName, lastName = lastName))
    } yield updated

  def updateUser(
      user: KeycloakUser,
      expectedStatusCode: Int = 204
  ): EitherT[Future, ResultStatus, Option[KeycloakUser]] =
    for {
      updatedUser <- EitherT(
        put(
          s"$URL$BASE_PATH/admin/realms/$REALM/users/${user.id}",
          Json.toJson(user),
          expectedStatusCode
        )
      )
    } yield {
      if (expectedStatusCode == 204) {
        None
      } else {
        Some(Json.parse(updatedUser).as[KeycloakUser])
      }

    }

}
