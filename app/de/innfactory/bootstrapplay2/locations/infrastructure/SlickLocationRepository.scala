package de.innfactory.bootstrapplay2.locations.infrastructure

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.{EitherT, Validated}
import dbdata.Tables
import de.innfactory.play.smithy4play.TraceContext
import de.innfactory.bootstrapplay2.commons.infrastructure.BaseSlickRepository
import de.innfactory.bootstrapplay2.commons.results.errors.Errors.BadRequest
import de.innfactory.bootstrapplay2.locations.domain.interfaces.LocationRepository
import de.innfactory.bootstrapplay2.locations.domain.models.{Location, LocationCompanyId, LocationId}
import de.innfactory.bootstrapplay2.locations.infrastructure.mapper.LocationMapper._
import de.innfactory.play.controller.ResultStatus
import slick.jdbc.JdbcBackend.Database
import slick.lifted.{Compiled, Rep}
import de.innfactory.play.db.codegen.XPostgresProfile.api._
import play.api.inject.ApplicationLifecycle
import slick.jdbc.{ResultSetConcurrency, ResultSetType}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

private[locations] class SlickLocationRepository @Inject() (db: Database, lifecycle: ApplicationLifecycle)(implicit
    ec: ExecutionContext
) extends BaseSlickRepository(db)
    with LocationRepository {

  private val queryById = (id: LocationId) => Compiled(Tables.Location.filter(_.id === id.value))
  private val queryByCompanyId = (id: LocationCompanyId) => Compiled(Tables.Location.filter(_.company === id.value))

  def getAllLocationsByCompany(
      companyId: LocationCompanyId
  )(implicit rc: TraceContext): EitherT[Future, ResultStatus, Seq[Location]] =
    lookupSequenceGeneric(
      queryByCompanyId(companyId).result
    )

  def getAllLocationsAsSource(implicit rc: TraceContext): Source[Location, NotUsed] = {
    val publisher = db
      .stream(
        Tables.Location.result
          .withStatementParameters(
            rsType = ResultSetType.ForwardOnly,
            rsConcurrency = ResultSetConcurrency.ReadOnly,
            fetchSize = 1000
          )
          .transactionally
      )
      .mapResult(locationRowToLocations)
    Source.fromPublisher(publisher)
  }

  def getById(locationId: LocationId)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Location] =
    lookupGeneric(queryById(locationId).result.headOption)

  def createLocation(location: Location)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Location] =
    createGeneric(
      location,
      l => (Tables.Location returning Tables.Location) += l
    )

  def updateLocation(location: Location)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Location] =
    for {
      updated <-
        updateGeneric(
          queryById(location.id).result.headOption,
          (l: Location) => Tables.Location insertOrUpdate locationToLocationRow(l),
          (old: Location) => old.patch(location)
        )

    } yield updated

  def deleteLocation(
      locationId: LocationId
  )(implicit rc: TraceContext): EitherT[Future, ResultStatus, Boolean] =
    deleteGeneric(
      queryById(locationId).result.headOption,
      queryById(locationId).delete
    )

  lifecycle.addStopHook(() =>
    Future.successful {
      close()
    }
  )

}
