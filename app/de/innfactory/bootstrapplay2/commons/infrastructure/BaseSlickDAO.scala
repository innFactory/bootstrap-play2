package de.innfactory.bootstrapplay2.commons.infrastructure

import akka.stream.scaladsl.Source
import cats.data.EitherT
import cats.implicits._
import cats.syntax._
import dbdata.Tables
import de.innfactory.bootstrapplay2.commons.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.commons.implicits.FutureTracingImplicits.EnhancedFuture
import de.innfactory.bootstrapplay2.commons.results.Results.{ Result, ResultStatus }
import de.innfactory.bootstrapplay2.commons.results.errors.Errors.{ BadRequest, DatabaseResult, NotFound }
import de.innfactory.bootstrapplay2.commons.implicits.OptionUtils._
import de.innfactory.bootstrapplay2.commons.TraceContext
import de.innfactory.bootstrapplay2.commons.implicits.EitherImplicits.EitherFuture
import de.innfactory.bootstrapplay2.commons.implicits.EitherTTracingImplicits.EnhancedTracingEitherT
import slick.dbio.{ DBIOAction, Effect, NoStream }
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.{ ResultSetConcurrency, ResultSetType }
import slick.jdbc.JdbcActionComponent
import slick.sql.FixedSqlStreamingAction
import slick.jdbc.{ ResultSetConcurrency, ResultSetType }
import slick.lifted.{ AbstractTable, TableQuery }

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.implicitConversions
import scala.util.Try

class BaseSlickDAO(db: Database)(implicit ec: ExecutionContext) extends ImplicitLogContext {

  def lookupGeneric[R, T](
    queryHeadOption: DBIOAction[Option[R], NoStream, Nothing]
  )(implicit rowToObject: R => T, rc: TraceContext): Future[Result[T]] = {
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
  )(implicit rowToObject: R => T, rc: TraceContext): Future[Option[T]] = {
    val queryResult: Future[Option[R]] = db.run(queryHeadOption).trace("lookupGenericOption")
    queryResult.map { res: Option[R] =>
      if (res.isDefined)
        Some(rowToObject(res.get))
      else
        None
    }
  }

  def countGeneric[R, T](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing]
  )(implicit rc: TraceContext): Future[Result[Int]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("countGeneric")
    queryResult.map(seq => Right(seq.length))
  }

  def lookupSequenceGeneric[R, T](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing]
  )(implicit rowToObject: R => T, rc: TraceContext): Future[Result[Seq[T]]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGeneric")
    queryResult.map { res: Seq[R] =>
      Right(res.map(rowToObject))
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
  )(implicit rowToObject: R => T, rc: TraceContext): Future[Result[Seq[T]]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGeneric")
    queryResult.map { res: Seq[R] =>
      Right(res.takeRight(count).map(rowToObject))
    }
  }

  def lookupSequenceGeneric[R, T](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing],
    from: Int,
    to: Int
  )(implicit rowToObject: R => T, rc: TraceContext): Future[Result[Seq[T]]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGeneric")
    queryResult.map { res: Seq[R] =>
      Right(res.slice(from, to + 1).map(rowToObject))
    }
  }

  def lookupSequenceGeneric[R, T, X, Z](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing],
    mapping: T => X,
    filter: X => Boolean,
    afterFilterMapping: X => Z
  )(implicit rowToObject: R => T, rc: TraceContext): Future[Result[Seq[Z]]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGeneric")
    queryResult.map { res: Seq[R] =>
      Right(res.map(rowToObject).map(mapping).filter(filter).map(afterFilterMapping))
    }
  }

  def lookupSequenceGeneric[R, T, Z](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing],
    sequenceMapping: Seq[T] => Z
  )(implicit rowToObject: R => T, rc: TraceContext): Future[Result[Z]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGeneric")
    queryResult.map { res: Seq[R] =>
      val sequence = res.map(rowToObject)
      Right(sequenceMapping(sequence))
    }
  }

  def updateGeneric[R, T](
    queryById: DBIOAction[Option[R], NoStream, Nothing],
    update: T => DBIOAction[Int, NoStream, Effect.Write],
    patch: T => T
  )(implicit rowToObject: R => T, rc: TraceContext): Future[Result[T]] = {
    val result = for {
      lookup        <- EitherT(db.run(queryById).map(_.toEither(BadRequest())).trace("updateGeneric lookup"))
      patchedObject <- EitherT(Future(Option(patch(rowToObject(lookup))).toEither(BadRequest())))
      patchResult   <-
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
  )(implicit rowToObject: R => T, objectToRow: T => R, rc: TraceContext): Future[Result[T]] = {
    val entityToSave = objectToRow(entity)
    val result       = for {
      _             <- EitherT(db.run(queryById).map(_.toInverseEither(BadRequest())).trace("createGeneric lookup"))
      createdObject <- EitherT(
                         Try(db.run(create(entityToSave)).trace("createGeneric create")).toEither
                           .leftMap(throwable => BadRequest(throwable.getMessage))
                           .foldEitherOfFuture
                       )
      res           <- EitherT.right[ResultStatus](Future(rowToObject(createdObject)))
    } yield res
    result.value
  }

  def createGeneric[R, T](
    entity: T,
    create: R => DBIOAction[R, NoStream, Effect.Write]
  )(implicit rowToObject: R => T, objectToRow: T => R, rc: TraceContext): Future[Result[T]] = {
    val entityToSave = objectToRow(entity)
    val result       = for {
      createdObject <- EitherT(
                         Try(db.run(create(entityToSave)).trace("createGeneric create")).toEither
                           .leftMap(throwable => BadRequest(throwable.getMessage))
                           .foldEitherOfFuture
                       )
      res           <- EitherT.right[ResultStatus](Future(rowToObject(createdObject)))
    } yield res
    result.value
  }

  def deleteGeneric[R, T](
    queryById: DBIOAction[Option[R], NoStream, Nothing],
    delete: DBIOAction[Int, NoStream, Effect.Write]
  )(implicit rc: TraceContext): Future[Result[Boolean]] = {

    val result = for {
      _              <- EitherT(
                          db.run(queryById)
                            .map(_.toEither(NotFound("entity not found")))
                        )
                          .trace("deleteGeneric lookup")
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
