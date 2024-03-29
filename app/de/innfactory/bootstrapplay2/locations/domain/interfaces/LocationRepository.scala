package de.innfactory.bootstrapplay2.locations.domain.interfaces

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.EitherT
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.companies.domain.models.CompanyId
import de.innfactory.play.tracing.TraceContext
import de.innfactory.play.controller.ResultStatus
import de.innfactory.bootstrapplay2.locations.domain.models.{Location, LocationId}
import de.innfactory.bootstrapplay2.locations.infrastructure.SlickLocationRepository

import scala.concurrent.Future

@ImplementedBy(classOf[SlickLocationRepository])
private[locations] trait LocationRepository {

  def getAllLocationsByCompany(companyId: CompanyId)(implicit
      rc: TraceContext
  ): EitherT[Future, ResultStatus, Seq[Location]]

  def getAllLocationsAsSource(implicit rc: TraceContext): Source[Location, NotUsed]

  def getById(locationId: LocationId)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Location]

  def createLocation(location: Location)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Location]

  def updateLocation(location: Location)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Location]

  def deleteLocation(locationId: LocationId)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Boolean]

}
