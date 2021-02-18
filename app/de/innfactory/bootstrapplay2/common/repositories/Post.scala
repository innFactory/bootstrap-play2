package de.innfactory.bootstrapplay2.common.repositories

import de.innfactory.bootstrapplay2.common.request.RequestContext
import de.innfactory.bootstrapplay2.common.results.Results.Result
import scala.concurrent.Future

trait Post[RC <: RequestContext, T] {
  def post(entity: T)(implicit rc: RC): Future[Result[T]]
}
