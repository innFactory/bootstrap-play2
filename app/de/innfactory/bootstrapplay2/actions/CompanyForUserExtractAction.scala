package de.innfactory.bootstrapplay2.actions

import cats.implicits.catsSyntaxEitherId
import com.google.inject.Inject
import de.innfactory.bootstrapplay2.common.authorization.FirebaseEmailExtractor
import de.innfactory.bootstrapplay2.common.request.TraceContext
import de.innfactory.bootstrapplay2.db.CompaniesDAO
import de.innfactory.bootstrapplay2.models.api.Company
import de.innfactory.play.tracing.{ RequestWithTrace, TraceRequest, UserExtractionActionBase }
import io.opencensus.trace.Span
import play.api.Environment
import play.api.mvc.Results.{ Forbidden, Unauthorized }
import play.api.mvc.{ AnyContent, BodyParsers, Request, Result, WrappedRequest }

import scala.concurrent.{ ExecutionContext, Future }

class RequestWithCompany[A](
  val company: Company,
  val email: Option[String],
  val request: Request[A],
  val traceSpan: Span
) extends WrappedRequest[A](request)
    with TraceRequest[A]

class CompanyForUserExtractAction @Inject() (
  companiesDAO: CompaniesDAO,
  firebaseEmailExtractor: FirebaseEmailExtractor[Any]
)(implicit executionContext: ExecutionContext, parser: BodyParsers.Default, environment: Environment)
    extends UserExtractionActionBase[RequestWithTrace, RequestWithCompany] {

  override def extractUserAndCreateNewRequest[A](request: RequestWithTrace[A])(implicit
    environment: Environment,
    parser: BodyParsers.Default,
    executionContext: ExecutionContext
  ): Future[Either[Result, RequestWithCompany[A]]] =
    Future.successful {
      val result: Option[Future[Option[Company]]] = for {
        email <- firebaseEmailExtractor.extractEmail(request)
      } yield for {
        user <- companiesDAO.internal_lookupByEmail(email)(new TraceContext(request.traceSpan))
      } yield user
      result match {
        case Some(v) =>
          v.map {
            case Some(value) =>
              new RequestWithCompany(
                value,
                firebaseEmailExtractor.extractEmail(request),
                request.request,
                request.traceSpan
              ).asRight[Result]
            case None        => Forbidden("").asLeft[RequestWithCompany[A]]
          }
        case None    =>
          Future(
            Forbidden("").asLeft[RequestWithCompany[A]]
          )
      }
    }.flatten
}
