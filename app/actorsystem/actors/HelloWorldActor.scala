package actorsystem.actors

import akka.actor.typed.scaladsl.{ ActorContext, Behaviors, StashBuffer }
import akka.actor.typed.Behavior
import play.api.Logger
import actorsystem.actors.commands.{
  Command,
  QueryError,
  QueryHelloWorld,
  QueryHelloWorldResult,
  ResponseQueryHelloWorld,
  ResponseQueryHelloWorldError
}

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

object HelloWorldActor {
  private val actorLogger = Logger("ActorSystemLogger").logger
  def apply(): Behavior[Command] =
    Behaviors.withStash(100) { buffer =>
      Behaviors.setup[Command] { context =>
        new HelloWorldActor(actorLogger, context, buffer)
          .ready() // Set initial state to "ready"
      }
    }
}

class HelloWorldActor(
  actorLogger: org.slf4j.Logger,
  context: ActorContext[Command],
  buffer: StashBuffer[Command],
) {
  implicit val ec: ExecutionContext = context.executionContext

  private def queryHelloWorld(query: QueryHelloWorld): Future[Command] = {
    Thread.sleep(100) // Long running !!!
    if (query.query == "hello")
      Future(QueryHelloWorldResult(ResponseQueryHelloWorld(query.query, query.query + " you"), query.replyTo))
    else
      Future(
        QueryHelloWorldResult(ResponseQueryHelloWorldError(query.query, "the query was not 'hello'"), query.replyTo)
      )
  }

  private def processQueryHelloWorldToBehavior(message: QueryHelloWorld): Behavior[Command] = {
    actorLogger.debug("Hello World Actor " + context.self.path.name + " received query " + message.query)
    context.pipeToSelf(
      queryHelloWorld(message)
    ) {
      case Success(success) => success
      case Failure(_)       => QueryError(message.query, message.replyTo)
    }
    query()
  }

  /**
   * Ready to receive requests
   * Will process QueryHelloWorld
   * @return
   */
  def ready(): Behavior[Command] =
    Behaviors.receiveMessage {
      case message: QueryHelloWorld => {
        processQueryHelloWorldToBehavior(message)
      }
      case _ => Behaviors.same
    }

  /**
   * When in Query Behavior only process QueryResult and QueryError
   * All other messages will get stashed for later processing after query state is over
   * @return Behavior[Command]
   */
  def query(): Behavior[Command] =
    Behaviors.receiveMessage {
      case result: QueryHelloWorldResult => {
        result.response match {
          case responseQueryHelloWorld: ResponseQueryHelloWorld => {
            result.replyTo ! responseQueryHelloWorld // Complete request with replyTo
            buffer.unstashAll(ready())               // Unstash all messages to process and set behavior to "ready" with prevData
          }
          case err: ResponseQueryHelloWorldError => {
            result.replyTo ! err       // Complete request with replyTo
            buffer.unstashAll(ready()) // Unstash all messages to process and set behavior to "ready" without prevData
          }
        }
      }
      case error: QueryError => {
        error.replyTo ! ResponseQueryHelloWorldError(error.query, "Error Query")
        ready()
      }
      case other => {
        buffer.stash(other)
        Behaviors.same
      }
    }
}
