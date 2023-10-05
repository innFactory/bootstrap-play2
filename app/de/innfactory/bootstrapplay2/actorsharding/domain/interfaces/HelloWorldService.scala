package de.innfactory.bootstrapplay2.actorsharding.domain.interfaces

import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.actorsharding.domain.services.HelloWorldServiceImpl
import de.innfactory.bootstrapplay2.actorsystem.domain.commands.Response

import scala.concurrent.Future

@ImplementedBy(classOf[HelloWorldServiceImpl])
trait HelloWorldService {
  def queryHelloWorld(query: String): Future[Response]
}
