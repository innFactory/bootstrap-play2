package de.innfactory.bootstrapplay2.locations.domain.interfaces

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.EitherT
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.commons.RequestContextWithUser
import de.innfactory.play.controller.ResultStatus
import de.innfactory.bootstrapplay2.locations.domain.models.{Location, LocationCompanyId, LocationId}
import de.innfactory.bootstrapplay2.locations.domain.services.DomainLocationService

import scala.concurrent.Future

@ImplementedBy(classOf[DomainLocationService])
trait LocationService {

  def getAllByCompany(locationCompanyId: LocationCompanyId)(implicit
      rc: RequestContextWithUser
  ): EitherT[Future, ResultStatus, Seq[Location]]

  def getAllAsStream()(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Source[Location, NotUsed]]

  def getById(id: LocationId)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Location]

  def updateLocation(company: Location)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Location]

  def createLocation(company: Location)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Location]

  def deleteLocation(id: LocationId)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Boolean]

}
