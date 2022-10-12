package de.innfactory.bootstrapplay2.actorsharding.application

import cats.data.EitherT
import com.typesafe.config.Config
import de.innfactory.bootstrapplay2.actorsharding.domain.interfaces.HelloWorldService
import de.innfactory.bootstrapplay2.actorsystem.domain.commands.{ResponseQueryHelloWorld, ResponseQueryHelloWorldError}
import de.innfactory.bootstrapplay2.api.{ActorShardingAPIController, HelloworldViaShardingResponse}
import de.innfactory.bootstrapplay2.application.controller.BaseController
import de.innfactory.play.results.errors.Errors.BadRequest
import de.innfactory.play.tracing.ImplicitLogContext
import de.innfactory.smithy4play.{AutoRouting, ContextRoute}
import play.api.Application
import play.api.mvc.ControllerComponents

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@AutoRouting
@Singleton
class ActorShardingHelloWorldController @Inject() (
    helloWorldService: HelloWorldService
)(implicit ec: ExecutionContext, cc: ControllerComponents, app: Application, config: Config)
    extends BaseController
    with ImplicitLogContext
    with ActorShardingAPIController[ContextRoute] {

  override def helloworldViaSharding(query: String): ContextRoute[HelloworldViaShardingResponse] =
    Endpoint
      .execute(_ =>
        EitherT {
          val result = for {
            response <- helloWorldService.queryHelloWorld(query)
          } yield response
          result.map {
            case ResponseQueryHelloWorld(_, answer)     => Right(HelloworldViaShardingResponse(answer))
            case ResponseQueryHelloWorldError(_, error) => Left(BadRequest(error))
          }
        }
      )
      .complete
}
