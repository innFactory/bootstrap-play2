package de.innfactory.bootstrapplay2.actorsystem.application
import cats.data.EitherT
import de.innfactory.bootstrapplay2.actorsystem.domain.commands.{ResponseQueryHelloWorld, ResponseQueryHelloWorldError}
import de.innfactory.bootstrapplay2.actorsystem.domain.interfaces.HelloWorldService
import de.innfactory.bootstrapplay2.api.{ActorSystemAPIController, HelloworldViaSystemResponse}
import de.innfactory.play.smithy4play.ImplicitLogContext
import de.innfactory.smithy4play.{AutoRouting, ContextRoute}
import play.api.Application
import de.innfactory.bootstrapplay2.application.controller.BaseController
import de.innfactory.play.results.errors.Errors.BadRequest
import play.api.mvc.ControllerComponents

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@AutoRouting
@Singleton
class ActorSystemHelloWorldController @Inject() (
    helloWorldService: HelloWorldService
)(implicit ec: ExecutionContext, cc: ControllerComponents, app: Application)
    extends BaseController
    with ImplicitLogContext
    with ActorSystemAPIController[ContextRoute] {

  override def helloworldViaSystem(query: String): ContextRoute[HelloworldViaSystemResponse] =
    Endpoint
      .execute(_ =>
        EitherT {
          val result = for {
            response <- helloWorldService.queryHelloWorld(query)
          } yield response
          result.map {
            case ResponseQueryHelloWorld(_, answer)     => Right(HelloworldViaSystemResponse(answer))
            case ResponseQueryHelloWorldError(_, error) => Left(BadRequest(error))
          }
        }
      )
      .complete
}
