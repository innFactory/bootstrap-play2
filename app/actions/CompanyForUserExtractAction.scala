package actions

import com.google.inject.Inject
import common.authorization.FirebaseEmailExtractor
import db.CompaniesDAO
import models.api.Company
import play.api.mvc.{ ActionBuilder, ActionTransformer, AnyContent, BodyParsers, Request, WrappedRequest }

import scala.concurrent.{ ExecutionContext, Future }

class RequestWithCompany[A](val company: Option[Company], val email: Option[String], request: Request[A])
    extends WrappedRequest[A](request)

class CompanyForUserExtractAction @Inject()(
  val parser: BodyParsers.Default,
  companiesDAO: CompaniesDAO,
  firebaseEmailExtractor: FirebaseEmailExtractor[Any]
)(implicit val executionContext: ExecutionContext)
    extends ActionBuilder[RequestWithCompany, AnyContent]
    with ActionTransformer[Request, RequestWithCompany] {
  def transform[A](request: Request[A]): Future[RequestWithCompany[A]] =
    Future.successful {
      val result: Option[Future[Option[Company]]] = for {
        email <- firebaseEmailExtractor.extractEmail(request)
      } yield
        for {
          user <- companiesDAO.internal_lookupByEmail(email)
        } yield user

      result match {
        case Some(v) => {
          v.map(new RequestWithCompany(_, firebaseEmailExtractor.extractEmail(request), request))
        }
        case None => Future(new RequestWithCompany(None, firebaseEmailExtractor.extractEmail(request), request))
      }
    }.flatten
}
