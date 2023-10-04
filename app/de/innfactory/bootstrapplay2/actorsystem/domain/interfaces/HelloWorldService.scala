package de.innfactory.bootstrapplay2.actorsystem.domain.interfaces

import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.actorsystem.domain.commands.Response
import de.innfactory.bootstrapplay2.actorsystem.domain.services.HelloWorldServiceImpl

import scala.concurrent.Future

@ImplementedBy(classOf[HelloWorldServiceImpl])
trait HelloWorldService {
  def queryHelloWorld(query: String): Future[Response]
}
