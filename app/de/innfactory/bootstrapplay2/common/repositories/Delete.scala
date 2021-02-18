package de.innfactory.bootstrapplay2.common.repositories

import de.innfactory.bootstrapplay2.common.request.RequestContext
import de.innfactory.bootstrapplay2.common.results.Results.Result
import scala.concurrent.Future

trait Delete[ID, RC <: RequestContext, T] {
  def delete(id: ID)(implicit rc: RC): Future[Result[T]]
}
