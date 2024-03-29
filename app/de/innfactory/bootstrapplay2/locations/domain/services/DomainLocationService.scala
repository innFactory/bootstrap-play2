package de.innfactory.bootstrapplay2.locations.domain.services

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.EitherT
import de.innfactory.bootstrapplay2.commons.RequestContextWithUser
import de.innfactory.bootstrapplay2.companies.domain.models.CompanyId
import de.innfactory.play.controller.ResultStatus
import de.innfactory.bootstrapplay2.locations.domain.interfaces.{LocationRepository, LocationService}
import de.innfactory.bootstrapplay2.locations.domain.models.{Location, LocationId}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

private[locations] class DomainLocationService @Inject() (locationRepository: LocationRepository)(implicit
    ec: ExecutionContext
) extends LocationService {

  def getAllByCompany(companyId: CompanyId)(implicit
      rc: RequestContextWithUser
  ): EitherT[Future, ResultStatus, Seq[Location]] =
    locationRepository.getAllLocationsByCompany(companyId)

  def getAllAsStream()(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Source[Location, NotUsed]] =
    for {
      result <- EitherT.right[ResultStatus](Future(locationRepository.getAllLocationsAsSource))
    } yield result

  def getById(id: LocationId)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Location] =
    locationRepository.getById(id)

  def updateLocation(company: Location)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Location] =
    locationRepository.updateLocation(company)

  def createLocation(company: Location)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Location] =
    locationRepository.createLocation(company)

  def deleteLocation(id: LocationId)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Boolean] =
    locationRepository.deleteLocation(id)
}
