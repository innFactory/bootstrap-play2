package de.innfactory.bootstrapplay2.commons.results
import akka.stream.scaladsl.Source
import de.innfactory.bootstrapplay2.commons.results.errors.Errors._
import de.innfactory.play.controller.ResultStatus
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{AnyContent, Request, Results => MvcResults}

import scala.concurrent.{ExecutionContext, Future}

object Results {

  type Result[T] = Either[ResultStatus, T]

}
