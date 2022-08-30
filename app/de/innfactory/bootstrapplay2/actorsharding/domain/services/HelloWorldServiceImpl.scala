package de.innfactory.bootstrapplay2.actorsharding.domain.services

import akka.actor._
import akka.actor.typed.Scheduler
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}
import akka.util.Timeout
import de.innfactory.bootstrapplay2.actorsharding.domain.common.Sharding
import de.innfactory.bootstrapplay2.actorsharding.domain.interfaces.HelloWorldService
import de.innfactory.bootstrapplay2.actorsystem.domain.commands.{Command, QueryHelloWorld, Response}
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern.Askable
import de.innfactory.bootstrapplay2.actorsystem.domain.actors.HelloWorldActor

import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HelloWorldServiceImpl @Inject() (
)(implicit ec: ExecutionContext, system: ActorSystem, sharding: Sharding)
    extends HelloWorldService {

  implicit val timeout: Timeout = sharding.timeout
  implicit private val scheduler: Scheduler = sharding.getScheduler
  private val clusterShard: ClusterSharding = sharding.getSharding

  val helloWorldTag = "PLAN_CONVERSION"
  val helloWorldTypeKey: EntityTypeKey[Command] =
    EntityTypeKey[Command](helloWorldTag)

  val helloWorldShardRegion: ActorRef[ShardingEnvelope[Command]] =
    clusterShard.init(
      Entity(helloWorldTypeKey)(createBehavior = entityContext => HelloWorldActor())
    )

  def queryHelloWorld(query: String): Future[Response] = {
    val result = helloWorldShardRegion.ask((ref: akka.actor.typed.ActorRef[Response]) =>
      ShardingEnvelope.apply(
        "shardingEnvelopeId",
        QueryHelloWorld(query, ref)
      )
    )
    result
  }
}
