package de.innfactory.bootstrapplay2.commons.application.actions

import com.google.inject.Inject
import de.innfactory.bootstrapplay2.commons.logging.LogContext
import de.innfactory.bootstrapplay2.commons.application.actions.models.RequestWithUser
import de.innfactory.play.tracing.TracingAction
import play.api.Environment
import play.api.mvc._

import scala.concurrent.ExecutionContext

class TracingUserAction @Inject() (
    val parser: BodyParsers.Default,
    userExtractionAction: UserExtractionAction,
    traceAction: TracingAction,
    implicit val environment: Environment
)(implicit val executionContext: ExecutionContext) {
  def apply()(implicit logContext: LogContext): ActionBuilder[RequestWithUser, AnyContent] = {
    val owningMethodName = Thread.currentThread.getStackTrace()(2).getMethodName
    traceAction(logContext.className + " " + owningMethodName).andThen(userExtractionAction)
  }

}
