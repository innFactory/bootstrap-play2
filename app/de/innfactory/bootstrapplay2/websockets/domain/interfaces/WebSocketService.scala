package de.innfactory.bootstrapplay2.websockets.domain.interfaces

import akka.stream.scaladsl.Flow
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.websockets.domain.DomainWebSocketService

@ImplementedBy(classOf[DomainWebSocketService])
trait WebSocketService {
  def socket: Flow[Any, Nothing, _]
}
