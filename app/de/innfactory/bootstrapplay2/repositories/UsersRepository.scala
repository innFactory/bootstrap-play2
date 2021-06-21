package de.innfactory.bootstrapplay2.repositories

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.implicits._
import cats.data.{ EitherT, Validated }
import com.google.inject.ImplementedBy
import com.typesafe.config.Config
import de.innfactory.bootstrapplay2.common.authorization.UsersAuthActions
import de.innfactory.bootstrapplay2.common.authorization.CompanyAuthorizationMethods
import de.innfactory.bootstrapplay2.common.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.common.request.{ RequestContext, RequestContextWithUser }
import de.innfactory.bootstrapplay2.common.results.Results.{ Result, ResultStatus }
import de.innfactory.bootstrapplay2.common.results.errors.Errors.{ BadRequest, TokenExpiredError, TokenValidationError }
import de.innfactory.bootstrapplay2.db.{ CompaniesDAO, UserResetTokensDAO }
import de.innfactory.bootstrapplay2.services.firebase.FirebaseUserService
import de.innfactory.bootstrapplay2.services.firebase.models.{ User, UserPasswordResetTokens, UserUpsertRequest }
import de.innfactory.bootstrapplay2.services.firebase.utils.Utils._

import java.nio.charset.StandardCharsets
import java.security.{ MessageDigest, SecureRandom }
import javax.inject.Inject
import org.apache.commons.codec.binary.Base64
import org.joda.time.DateTime

import scala.concurrent.{ ExecutionContext, Future }

@ImplementedBy(classOf[UsersRepositoryImpl])
trait UsersRepository {

  def getUsersForCompany(companyId: Long)(implicit
    rc: RequestContextWithUser
  ): Future[Either[ResultStatus, Source[User, NotUsed]]]

  def upsertUser(
    userUpsertRequest: UserUpsertRequest
  )(implicit
    rc: RequestContextWithUser
  ): Future[Result[User]]

  def patchUser(
    userUpsertRequest: UserUpsertRequest
  )(implicit
    rc: RequestContextWithUser
  ): Future[Result[User]]

  def resetPassword(token: String, password: String, userId: String)(implicit
    rc: RequestContext
  ): Future[Result[User]]

}

class UsersRepositoryImpl @Inject() (
  firebaseUserService: FirebaseUserService,
  companiesRepository: CompaniesRepository,
  userResetTokensDAO: UserResetTokensDAO,
  companiesDAO: CompaniesDAO,
  config: Config
)(implicit ec: ExecutionContext)
    extends UsersRepository
    with ImplicitLogContext {

  def getUsersForCompany(companyId: Long)(implicit
    rc: RequestContextWithUser
  ): Future[Either[ResultStatus, Source[User, NotUsed]]] = {
    val result = for {
      company <- EitherT(companiesRepository.lookup(companyId))
      _       <- EitherT(Future(CompanyAuthorizationMethods.canGet(company)))
    } yield firebaseUserService.getUsersSource.filter { u =>
      val user = u.toFirebaseUser
      user.isCompanyAdmin(companyId)
    }.map(_.toUser)
    result.value
  }

  private def createNewUser(email: String)(implicit rc: RequestContextWithUser): Result[User] = {
    val result = for {
      user <- firebaseUserService.createUser(email)
    } yield user
    result
  }

  def resetPassword(token: String, password: String, userId: String)(implicit
    rc: RequestContext
  ): Future[Result[User]] = {
    val result = for {
      resetTokenForUser <- EitherT(userResetTokensDAO.getForUser(userId))
      _                 <- EitherT(
                             Future(
                               Validated
                                 .cond(resetTokenForUser.token == token, (), List(TokenValidationError("Not Valid")))
                                 .combine(
                                   Validated.cond(
                                     resetTokenForUser.validUntil.isAfter(DateTime.now()),
                                     (),
                                     List(TokenExpiredError("Expired"))
                                   )
                                 )
                                 .leftMap[ResultStatus](e => e.headOption.getOrElse(BadRequest("InvalidToken")))
                                 .toEither
                             )
                           )
      user              <- EitherT(Future(firebaseUserService.setUserPassword(userId, password)))
      _                 <- EitherT(userResetTokensDAO.delete(resetTokenForUser))
    } yield user
    result.value
  }

  def getResetLinkAndSendEmail(createdUser: User)(implicit rc: RequestContext): EitherT[Future, ResultStatus, Unit] = {
    val uiURI       = "localhost"
    val random      = new SecureRandom()
    val randomBytes = random.generateSeed(10)
    val digest      = MessageDigest.getInstance("SHA-256")
    val hash        = digest.digest(createdUser.id.getBytes(StandardCharsets.UTF_8) ++ randomBytes)
    val encoded     = Base64.encodeBase64URLSafeString(hash)
    for {
      token    <- EitherT(
                    userResetTokensDAO.create(
                      UserPasswordResetTokens(createdUser.id, encoded, DateTime.now(), DateTime.now().plusDays(10))
                    )
                  )
      resetLink = s"$uiURI/reset?token=" + token.token + "&uid=" + createdUser.id
      x         = print(resetLink)
      // Optionally Send Email _        <- EitherT(emailSendService.sendWelcomeEmail(createdUser.email, resetLink))
    } yield ()
  }

  def patchUser(userUpsertRequest: UserUpsertRequest)(implicit
    rc: RequestContextWithUser
  ): Future[Result[User]] = {
    val result = for {
      _            <- EitherT(UsersAuthActions.updateUser(userUpsertRequest)(rc, companiesDAO, ec))
      oldUser      <- EitherT(Future(firebaseUserService.getUser(userUpsertRequest.id.get)))
      upsertedUser <-
        EitherT(
          Future(firebaseUserService.upsertUser(userUpsertRequest.copy(id = Some(oldUser.id)).toUser.get, oldUser))
        )
    } yield upsertedUser
    result.value
  }

  def upsertUser(
    userUpsertRequest: UserUpsertRequest
  )(implicit
    rc: RequestContextWithUser
  ): Future[Result[User]] = {
    val result: EitherT[Future, ResultStatus, User] = for {
      _            <- EitherT(UsersAuthActions.creatUser(userUpsertRequest)(rc, companiesDAO, ec))
      userByEmail  <- EitherT.right(Future(firebaseUserService.getUserByEmail(userUpsertRequest.email).toOption))
      oldUser      <- EitherT(Future({
                        if (userByEmail.isDefined)
                          userByEmail.get.asRight[ResultStatus]
                        else
                          createNewUser(userUpsertRequest.email)
                      }))
      _            <-
        EitherT(
          Future(
            Validated
              .cond(
                oldUser.claims.isEmpty,
                (),
                BadRequest("User exists and already has permissions. Use update method")
              )
              .toEither
          )
        )
      upsertedUser <-
        EitherT(
          Future(firebaseUserService.upsertUser(userUpsertRequest.copy(id = Some(oldUser.id)).toUser.get, oldUser))
        )
      _            <- {
        if (userByEmail.isEmpty)
          getResetLinkAndSendEmail(upsertedUser) // Only send Email if new User
        else
          EitherT(Future(().asRight[ResultStatus]))
      }
    } yield upsertedUser
    result.value
  }

}
