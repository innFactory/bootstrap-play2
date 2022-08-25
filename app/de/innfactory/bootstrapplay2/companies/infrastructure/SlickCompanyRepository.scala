package de.innfactory.bootstrapplay2.companies.infrastructure

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.{EitherT, Validated}
import dbdata.Tables
import de.innfactory.bootstrapplay2.commons.RequestContext
import de.innfactory.bootstrapplay2.commons.filteroptions.FilterOptionUtils
import de.innfactory.play.controller.ResultStatus
import de.innfactory.bootstrapplay2.commons.infrastructure.BaseSlickDAO
import de.innfactory.bootstrapplay2.commons.results.errors.Errors.BadRequest
import de.innfactory.bootstrapplay2.companies.domain.interfaces.CompanyRepository
import de.innfactory.bootstrapplay2.companies.domain.models.{Company, CompanyId}
import de.innfactory.bootstrapplay2.companies.domain.models.Company.patch
import de.innfactory.bootstrapplay2.companies.infrastructure.mapper.CompanyMapper._
import de.innfactory.play.db.codegen.XPostgresProfile.api._
import de.innfactory.play.slick.enhanced.utils.filteroptions.FilterOptions
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.{ResultSetConcurrency, ResultSetType}
import de.innfactory.play.slick.enhanced.query.EnhancedQuery._
import de.innfactory.play.smithy4play.TraceContext

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

private[companies] class SlickCompanyRepository @Inject() (db: Database)(implicit ec: ExecutionContext)
    extends BaseSlickDAO(db)
    with CompanyRepository {

  private val queryById = (id: CompanyId) => Compiled(Tables.Company.filter(_.id === id.value))

  private def queryFromFiltersSeq(filter: Seq[FilterOptions[Tables.Company, _]]) =
    Compiled(Tables.Company.filterOptions(filter))

  override def getAll()(implicit rc: TraceContext): EitherT[Future, ResultStatus, Seq[Company]] =
    lookupSequenceGeneric(Tables.Company.result)

  def getAllForGraphQL(filterOptions: Option[String])(implicit rc: RequestContext): Future[Seq[Company]] = {
    val filter = FilterOptionUtils.optionStringToFilterOptions(filterOptions)
    lookupSequenceGenericRawSequence(
      queryFromFiltersSeq(filter).result
    )
  }

  def getAllCompaniesAsSource(implicit rc: TraceContext): Source[Company, NotUsed] = {
    val publisher = db
      .stream(
        Tables.Company.result
          .withStatementParameters(
            rsType = ResultSetType.ForwardOnly,
            rsConcurrency = ResultSetConcurrency.ReadOnly,
            fetchSize = 1000
          )
          .transactionally
      )
      .mapResult(companyRowToCompany)
    Source.fromPublisher(publisher)
  }

  override def getById(companyId: CompanyId)(implicit
      rc: TraceContext
  ): EitherT[Future, ResultStatus, Company] =
    lookupGeneric(queryById(companyId).result.headOption)

  override def createCompany(company: Company)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Company] =
    createGeneric(
      company,
      row => (Tables.Company returning Tables.Company) += row
    )

  def updateCompany(company: Company)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Company] =
    for {
      _ <- EitherT(Future(Validated.cond(company.id.isDefined, (), BadRequest("")).toEither))
      updated <-
        updateGeneric(
          queryById(company.id.get).result.headOption,
          (c: Company) => Tables.Company insertOrUpdate companyToCompanyRow(c),
          oldCompany => patch(company, oldCompany)
        )
    } yield updated

  def deleteCompany(id: CompanyId)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Boolean] =
    deleteGeneric(
      queryById(id).result.headOption,
      queryById(id).delete
    )
}
