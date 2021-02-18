package de.innfactory.bootstrapplay2.common.repositories

import de.innfactory.bootstrapplay2.common.request.RequestContext
import de.innfactory.bootstrapplay2.common.results.Results.Result

import scala.concurrent.Future

trait All[RC <: RequestContext, T] {
  def all(implicit rc: RC): Future[Result[Seq[T]]]
}
