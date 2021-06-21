package de.innfactory.bootstrapplay2.repositories

import java.util.UUID
import cats.implicits._
import cats.data.EitherT
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.common.authorization.CompanyAuthorizationMethods
import de.innfactory.bootstrapplay2.common.implicits.EitherTTracingImplicits.{ EnhancedTracingEitherT, TracedT }
import de.innfactory.bootstrapplay2.common.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.common.repositories.{ All, Delete, Lookup, Patch, Post }
import de.innfactory.bootstrapplay2.common.request.{ RequestContext, RequestContextWithUser }
import de.innfactory.bootstrapplay2.common.results.Results.{ Result, ResultStatus }
import de.innfactory.bootstrapplay2.common.results.errors.Errors.BadRequest
import de.innfactory.bootstrapplay2.common.utils.OptionUtils.EnhancedOption
import de.innfactory.bootstrapplay2.db.CompaniesDAO
import de.innfactory.bootstrapplay2.graphql.ErrorParserImpl
import de.innfactory.grapqhl.play.result.implicits.GraphQlResult.EnhancedFutureResult

import javax.inject.Inject
import de.innfactory.bootstrapplay2.models.api.Company
import de.innfactory.play.slick.enhanced.utils.filteroptions.FilterOptions
import play.api.mvc.AnyContent
import slick.basic.DatabasePublisher

import scala.concurrent.{ ExecutionContext, Future }

@ImplementedBy(classOf[CompaniesRepositoryImpl])
trait CompaniesRepository
    extends Lookup[Long, RequestContextWithUser, Company]
    with All[RequestContext, Company]
    with Patch[RequestContextWithUser, Company]
    with Post[RequestContextWithUser, Company]
    with Delete[Long, RequestContextWithUser, Company] {
  def allGraphQl(filter: Seq[FilterOptions[_root_.dbdata.Tables.Company, _]])(implicit
    rc: RequestContext
  ): Future[Seq[Company]]
  def streamedAll(implicit tc: RequestContext): Future[DatabasePublisher[Company]]
}

class CompaniesRepositoryImpl @Inject() (
  companiesDAO: CompaniesDAO
)(implicit ec: ExecutionContext, errorParser: ErrorParserImpl)
    extends CompaniesRepository
    with ImplicitLogContext {

  def lookup(id: Long)(implicit rc: RequestContextWithUser): Future[Result[Company]] = {
    val result = for {
      lookupResult <- EitherT(companiesDAO.lookup(id))
      _            <- EitherT(Future(CompanyAuthorizationMethods.canGet(lookupResult)))
    } yield lookupResult
    result.value
  }

  def all(implicit rc: RequestContext): Future[Result[Seq[Company]]] = {
    val result = for {
      lookupResult <- EitherT(companiesDAO.all.map(_.asRight[ResultStatus]))
    } yield lookupResult
    result.value
  }

  def streamedAll(implicit tc: RequestContext): Future[DatabasePublisher[Company]] = {
    val result = for {
      pub <- Future(companiesDAO.streamedAll)
    } yield pub
    result
  }

  def allGraphQl(
    filter: Seq[FilterOptions[_root_.dbdata.Tables.Company, _]]
  )(implicit rc: RequestContext): Future[Seq[Company]] = {
    val result = for {
      lookupResult <- EitherT(companiesDAO.allWithFilter(filter).map(_.asRight[ResultStatus]))
    } yield lookupResult
    result.value.completeOrThrow
  }

  def patch(company: Company)(implicit rc: RequestContextWithUser): Future[Result[Company]] = {
    val result = for {
      _             <- TracedT("Patch Company Repository before lookup") // Can be used as extra step
      oldCompany    <- EitherT(companiesDAO.lookup(company.id.getOrElse(0))).trace("Companies DAO Lookup")
      _             <- TracedT("Patch Company Repository after lookup")
      _             <- EitherT(Future(CompanyAuthorizationMethods.canUpdate(company))).trace("Authorization Method")
      companyUpdate <- EitherT(companiesDAO.update(company.copy(id = oldCompany.id))).trace("Companies DAO Update")
    } yield companyUpdate
    result.value
  }
  def post(company: Company)(implicit rc: RequestContextWithUser): Future[Result[Company]] = {
    val result: EitherT[Future, ResultStatus, Company] = for {
      _             <- EitherT({
                         if (company.id.isDefined) companiesDAO.lookup(company.id.get).map(_.toOption.toInverseEither(BadRequest()))
                         else Future(Right(()))
                       })
      _             <- EitherT(Future(CompanyAuthorizationMethods.canCreate()))
      createdResult <- EitherT(companiesDAO.create(company))
    } yield createdResult
    result.value
  }
  def delete(id: Long)(implicit rc: RequestContextWithUser): Future[Result[Company]] = {
    val result: EitherT[Future, ResultStatus, Company] = for {
      company <- EitherT(companiesDAO.lookup(id))
      _       <- EitherT(Future(CompanyAuthorizationMethods.canDelete(company)))
      _       <- EitherT(companiesDAO.delete(id))
    } yield company
    result.value
  }
}
