package de.innfactory.bootstrapplay2.actions

import com.google.inject.Inject
import de.innfactory.play.tracing.TracingAction
import play.api.Environment
import play.api.mvc._

import scala.concurrent.ExecutionContext

class TracingCompanyAction @Inject() (
  val parser: BodyParsers.Default,
  companyAction: CompanyForUserExtractAction,
  jwtValidationAction: JwtValidationAction,
  traceAction: TracingAction,
  implicit val environment: Environment
)(implicit val executionContext: ExecutionContext) {
  def apply(traceString: String): ActionBuilder[RequestWithCompany, AnyContent] =
    traceAction(traceString).andThen(jwtValidationAction).andThen(companyAction)
}
