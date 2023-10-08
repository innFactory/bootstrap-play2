package de.innfactory.bootstrapplay2.commons.infrastructure

import cats.data.EitherT
import cats.implicits._
import cats.syntax._
import com.typesafe.config.Config
import de.innfactory.play.results.errors.Errors.{BadRequest, DatabaseResult, NotFound}
import de.innfactory.bootstrapplay2.commons.implicits.EitherImplicits.EitherFuture
import de.innfactory.implicits.OptionUtils.EnhancedOption
import de.innfactory.play.controller.ResultStatus
import de.innfactory.play.smithy4play.ImplicitLogContext
import de.innfactory.play.tracing.TraceContext
import de.innfactory.play.tracing.implicits.EitherTTracingImplicits.EnhancedTracingEitherT
import de.innfactory.play.tracing.implicits.FutureTracingImplicits.EnhancedFuture
import io.opentelemetry.api.trace.Tracer
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

class BaseSlickRepository(db: Database)(implicit ec: ExecutionContext) extends ImplicitLogContext {

  def lookupGeneric[R, T](
      queryHeadOption: DBIOAction[Option[R], NoStream, Nothing]
  )(implicit rowToObject: R => T, rc: TraceContext): EitherT[Future, ResultStatus, T] = EitherT {
    val queryResult: Future[Option[R]] = db.run(queryHeadOption).trace("lookupGeneric")
    queryResult.map { res: Option[R] =>
      if (res.isDefined)
        Right(rowToObject(res.get))
      else
        Left(
          NotFound()
        )
    }
  }

  def lookupGenericOption[R, T](
      queryHeadOption: DBIOAction[Option[R], NoStream, Nothing]
  )(implicit rowToObject: R => T, rc: TraceContext): EitherT[Future, ResultStatus, Option[T]] =
    EitherT {
      val queryResult: Future[Option[R]] = db.run(queryHeadOption).trace("lookupGenericOption")
      queryResult.map { res: Option[R] =>
        if (res.isDefined)
          Some(rowToObject(res.get)).asRight[ResultStatus]
        else
          None.asRight[ResultStatus]
      }
    }

  def countGeneric[R, T](
      querySeq: DBIOAction[Seq[R], NoStream, Nothing]
  )(implicit rc: TraceContext): EitherT[Future, ResultStatus, Int] = EitherT {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("countGeneric")
    queryResult.map(seq => seq.length.asRight[ResultStatus])
  }

  def lookupSequenceGeneric[R, T](
      querySeq: DBIOAction[Seq[R], NoStream, Nothing]
  )(implicit rowToObject: R => T, rc: TraceContext): EitherT[Future, ResultStatus, Seq[T]] = EitherT {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGeneric")
    queryResult.map { res: Seq[R] =>
      res.map(rowToObject).asRight[ResultStatus]
    }
  }

  def lookupSequenceGenericRawSequence[R, T](
      querySeq: DBIOAction[Seq[R], NoStream, Nothing]
  )(implicit rowToObject: R => T, rc: TraceContext): Future[Seq[T]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGenericRawSequence")
    queryResult.map { res: Seq[R] =>
      res.map(rowToObject)
    }
  }

  def lookupSequenceGeneric[R, T](
      querySeq: DBIOAction[Seq[R], NoStream, Nothing],
      count: Int
  )(implicit rowToObject: R => T, rc: TraceContext): EitherT[Future, ResultStatus, Seq[T]] = EitherT {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGeneric")
    queryResult.map { res: Seq[R] =>
      res.takeRight(count).map(rowToObject).asRight[ResultStatus]
    }
  }

  def lookupSequenceGeneric[R, T](
      querySeq: DBIOAction[Seq[R], NoStream, Nothing],
      from: Int,
      to: Int
  )(implicit rowToObject: R => T, rc: TraceContext): EitherT[Future, ResultStatus, Seq[T]] = EitherT {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGeneric")
    queryResult.map { res: Seq[R] =>
      res.slice(from, to + 1).map(rowToObject).asRight[ResultStatus]
    }
  }

  def lookupSequenceGeneric[R, T, X, Z](
      querySeq: DBIOAction[Seq[R], NoStream, Nothing],
      mapping: T => X,
      filter: X => Boolean,
      afterFilterMapping: X => Z
  )(implicit rowToObject: R => T, rc: TraceContext): EitherT[Future, ResultStatus, Seq[Z]] = EitherT {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGeneric")
    queryResult.map { res: Seq[R] =>
      res.map(rowToObject).map(mapping).filter(filter).map(afterFilterMapping).asRight[ResultStatus]
    }
  }

  def lookupSequenceGeneric[R, T, Z](
      querySeq: DBIOAction[Seq[R], NoStream, Nothing],
      sequenceMapping: Seq[T] => Z
  )(implicit rowToObject: R => T, rc: TraceContext): EitherT[Future, ResultStatus, Z] = EitherT {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGeneric")
    queryResult.map { res: Seq[R] =>
      val sequence = res.map(rowToObject)
      sequenceMapping(sequence).asRight[ResultStatus]
    }
  }

  def updateGeneric[R, T](
      queryById: DBIOAction[Option[R], NoStream, Nothing],
      update: T => DBIOAction[Int, NoStream, Effect.Write],
      patch: T => T
  )(implicit rowToObject: R => T, rc: TraceContext): EitherT[Future, ResultStatus, T] = EitherT {
    // TODO rc.logIfDebug("", )
    val result = for {
      lookup <- EitherT(db.run(queryById).map(_.toEither(BadRequest())).trace("updateGeneric lookup"))
      patchedObject <- EitherT(Future(Option(patch(rowToObject(lookup))).toEither(BadRequest())))
      patchResult <-
        EitherT[Future, ResultStatus, T](
          db.run(update(patchedObject))
            .map { x =>
              if (x != 0) Right(patchedObject)
              else {
                rc.log.error("Database Result Updating entity")
                Left(
                  DatabaseResult("Could not update entity")
                )
              }
            }
            .trace("updateGeneric update")
        )
    } yield patchResult
    result.value
  }

  def createGeneric[R, T](
      entity: T,
      queryById: DBIOAction[Option[R], NoStream, Nothing],
      create: R => DBIOAction[R, NoStream, Effect.Write]
  )(implicit
      rowToObject: R => T,
      objectToRow: T => R,
      rc: TraceContext
  ): EitherT[Future, ResultStatus, T] = EitherT {
    val entityToSave = objectToRow(entity)
    val result = for {
      _ <- EitherT(db.run(queryById).map(_.toInverseEither(BadRequest())).trace("createGeneric lookup"))
      createdObject <- EitherT(
        Try(db.run(create(entityToSave)).trace("createGeneric create")).toEither
          .leftMap(throwable => BadRequest(throwable.getMessage))
          .foldEitherOfFuture
      )
      res <- EitherT.right[ResultStatus](Future(rowToObject(createdObject)))
    } yield res
    result.value
  }

  def createGeneric[R, T](
      entity: T,
      create: R => DBIOAction[R, NoStream, Effect.Write]
  )(implicit
      rowToObject: R => T,
      objectToRow: T => R,
      rc: TraceContext
  ): EitherT[Future, ResultStatus, T] = EitherT {
    val entityToSave = objectToRow(entity)
    val result = for {
      createdObject <- EitherT(
        Try(db.run(create(entityToSave)).trace("createGeneric create")).toEither
          .leftMap(throwable => BadRequest(throwable.getMessage))
          .foldEitherOfFuture
      )
      res <- EitherT.right[ResultStatus](Future(rowToObject(createdObject)))
    } yield res
    result.value
  }

  def deleteGeneric[R, T](
      queryById: DBIOAction[Option[R], NoStream, Nothing],
      delete: DBIOAction[Int, NoStream, Effect.Write]
  )(implicit rc: TraceContext): EitherT[Future, ResultStatus, Boolean] = EitherT {

    val result = for {
      _ <- EitherT(
        db.run(queryById)
          .map(_.toEither(NotFound("entity not found")))
          .trace("deleteGeneric lookup")
      )
      dbDeleteResult <- EitherT(
        Try(db.run(delete)).toEither
          .leftMap[ResultStatus](_ => DatabaseResult("Database constraint or foreign key"))
          .foldEitherOfFuture
          .map(t => t.map(_ => true))
      )
        .trace("deleteGeneric delete")
    } yield dbDeleteResult
    result.value
  }

  /**
   * Close dao
   * @return
   */
  def close(): Future[Unit] =
    Future.successful(db.close())

}
