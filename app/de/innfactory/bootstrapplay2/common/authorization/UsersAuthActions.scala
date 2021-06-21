package de.innfactory.bootstrapplay2.common.authorization

import cats.Id
import cats.data.{ EitherT, Writer, WriterT }
import de.innfactory.bootstrapplay2.common.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.common.request.{ RequestContext, RequestContextWithUser }
import de.innfactory.bootstrapplay2.common.results.Results.{ Result, ResultStatus }
import de.innfactory.bootstrapplay2.common.results.errors.Errors.Forbidden
import de.innfactory.bootstrapplay2.db.CompaniesDAO
import de.innfactory.bootstrapplay2.services.firebase.models.UserUpsertRequest
import de.innfactory.bootstrapplay2.services.firebase.utils.Utils._

import scala.concurrent.{ ExecutionContext, Future }

object UsersAuthActions extends ImplicitLogContext {

  private case class CanCreateClaimsResult(
    canCreateCompanyAdminClaim: Boolean,
    canCreateInnFactoryAdminClaim: Boolean
  )

  def creatUser(userUpsertRequest: UserUpsertRequest)(implicit
    rc: RequestContextWithUser,
    companiesDAO: CompaniesDAO,
    ec: ExecutionContext
  ): Future[Result[Boolean]] = {
    val claims = userUpsertRequest.claims

    val result = for {
      cr <- checkClaims(userUpsertRequest)
    } yield (
      cr._1,
      cr._2.canCreateInnFactoryAdminClaim &&
        cr._2.canCreateCompanyAdminClaim &&
        (claims.innFactoryAdmin.isDefined ||
          claims.companyAdmin.isDefined)
    )

    mapResult(result)
  }

  def updateUser(userUpsertRequest: UserUpsertRequest)(implicit
    rc: RequestContextWithUser,
    companiesDAO: CompaniesDAO,
    ec: ExecutionContext
  ): Future[Result[Boolean]] = {
    val result = for {
      cr <- checkClaims(userUpsertRequest)
    } yield (
      cr._1,
      cr._2.canCreateCompanyAdminClaim &&
        cr._2.canCreateInnFactoryAdminClaim
    )

    mapResult(result)
  }

  private def checkClaims(userUpsertRequest: UserUpsertRequest)(implicit
    rc: RequestContextWithUser,
    companiesDAO: CompaniesDAO,
    ec: ExecutionContext
  ): EitherT[Future, ResultStatus, (Writer[List[String], List[String]], CanCreateClaimsResult)] = {

    val claims = userUpsertRequest.claims

    def canCreateInnFactoryAdmin(writer: Writer[List[String], List[String]]) =
      if (claims.innFactoryAdmin.isDefined)
        if (rc.user.isInnFactoryAdmin)
          (writer.map(_ ++ List("User has Permissions to create innFactoryAdminClaim")), true)
        else
          (writer.tell(List("Not enough permissions to create innFactoryAdmin")), false)
      else
        (writer.map(_ ++ List("innFactoryAdmin not Set")), true)

    def canCreateCompanyAdmin(writer: Writer[List[String], List[String]]) =
      if (claims.companyAdmin.isDefined) {
        if (rc.user.isInnFactoryAdmin)
          (writer.map(_ ++ List("User has Permissions to create companyAdminClaim")), true)
        else
          (writer.tell(List("Not enough permissions to create CompanyAdmin")), false)
      } else
        (writer.map(_ ++ List("companyAdmin not Set")), true)

    val initialWriter = Writer.apply(List.empty[String], List.empty[String])

    val result: EitherT[Future, ResultStatus, (WriterT[Id, List[String], List[String]], CanCreateClaimsResult)] = for {
      canCreateInnFactoryAdminClaim <- EitherT.right(Future(canCreateInnFactoryAdmin(initialWriter)))
      canCreateCompanyAdmin         <- EitherT.right(Future(canCreateCompanyAdmin(canCreateInnFactoryAdminClaim._1)))
    } yield (
      canCreateCompanyAdmin._1,
      CanCreateClaimsResult(
        canCreateCompanyAdmin._2,
        canCreateInnFactoryAdminClaim._2
      )
    )
    result
  }

  private def mapResult(
    result: EitherT[Future, ResultStatus, (Writer[List[String], List[String]], Boolean)]
  )(implicit ec: ExecutionContext, rc: RequestContext): Future[Either[ResultStatus, Boolean]] =
    result.value.map {
      case Left(value)  => Left(value)
      case Right(value) =>
        value._2 match {
          case true  =>
            rc.log.debug(value._1.run._2.mkString(", "))
            Right(true)
          case false =>
            rc.log.warn(value._1.run._1.mkString(", "))
            Left(Forbidden(value._1.run._1.mkString(", ")))
        }
    }

}
