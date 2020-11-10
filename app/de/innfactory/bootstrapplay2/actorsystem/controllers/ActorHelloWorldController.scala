package de.innfactory.bootstrapplay2.actorsystem.controllers
import de.innfactory.bootstrapplay2.actorsystem.actors.commands.{
  ResponseQueryHelloWorld,
  ResponseQueryHelloWorldError
}
import de.innfactory.bootstrapplay2.actorsystem.services.HelloWorldService
import javax.inject.{ Inject, Singleton }
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class ActorHelloWorldController @Inject() (
  cc: ControllerComponents,
  helloWorldService: HelloWorldService
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def helloWorldActor(query: String): Action[AnyContent] =
    Action.async { implicit request =>
      val result = for {
        response <- helloWorldService.queryHelloWorld(query)
      } yield response
      result.map {
        case ResponseQueryHelloWorld(_, answer)     => Status(200)(answer)
        case ResponseQueryHelloWorldError(_, error) => Status(400)(error)
      }
    }

}
