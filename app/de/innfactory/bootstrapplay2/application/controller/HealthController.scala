package de.innfactory.bootstrapplay2.application.controller

import cats.data.EitherT
import com.typesafe.config.Config
import de.innfactory.bootstrapplay2.api.HealthAPIController
import de.innfactory.bootstrapplay2.commons.infrastructure.DatabaseHealthSocket
import de.innfactory.play.results.errors.Errors.InternalServerError
import de.innfactory.play.tracing.ImplicitLogContext
import de.innfactory.smithy4play.{AutoRouting, ContextRoute}
import play.api.Application
import play.api.mvc.ControllerComponents

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@AutoRouting
@Singleton
class HealthController @Inject() (
    databaseHealthSocket: DatabaseHealthSocket
)(implicit ec: ExecutionContext, cc: ControllerComponents, app: Application, config: Config)
    extends BaseController
    with ImplicitLogContext
    with HealthAPIController[ContextRoute] {

  def ping(): ContextRoute[Unit] = Endpoint
    .execute(_ =>
      EitherT.rightT(
        if (databaseHealthSocket.isConnectionOpen) Right("")
        else Left(InternalServerError("Database Connection Lost"))
      )
    )
    .complete

  override def liveness(): ContextRoute[Unit] = ping()

  override def readiness(): ContextRoute[Unit] = ping()
}
