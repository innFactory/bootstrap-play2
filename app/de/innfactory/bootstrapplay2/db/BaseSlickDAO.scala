package de.innfactory.bootstrapplay2.db

import de.innfactory.bootstrapplay2.common.utils.OptionUtils._
import cats.data.EitherT
import de.innfactory.play.db.codegen.XPostgresProfile
import de.innfactory.bootstrapplay2.common.results.Results.{ Result, ResultStatus }
import de.innfactory.bootstrapplay2.common.results.errors.Errors.{ BadRequest, DatabaseResult, NotFound }
import slick.jdbc.JdbcBackend.Database
import dbdata.Tables
import de.innfactory.bootstrapplay2.common.implicits.FutureTracingImplicits.EnhancedFuture
import de.innfactory.bootstrapplay2.common.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.common.request.TraceContext
import slick.dbio.{ DBIOAction, Effect, NoStream }

import scala.language.implicitConversions
import scala.concurrent.{ ExecutionContext, Future }

class BaseSlickDAO(db: Database)(implicit ec: ExecutionContext) extends Tables with ImplicitLogContext {

  override val profile = XPostgresProfile

  def lookupGeneric[R, T](
    queryHeadOption: DBIOAction[Option[R], NoStream, Nothing]
  )(implicit rowToObject: R => T, tc: TraceContext): Future[Result[T]] = {
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
  )(implicit rowToObject: R => T, tc: TraceContext): Future[Option[T]] = {
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
  )(implicit tc: TraceContext): Future[Result[Int]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("countGeneric")
    queryResult.map(seq => Right(seq.length))
  }

  def lookupSequenceGeneric[R, T](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing]
  )(implicit rowToObject: R => T, tc: TraceContext): Future[Result[Seq[T]]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGeneric")
    queryResult.map { res: Seq[R] =>
      Right(res.map(rowToObject))
    }
  }

  def lookupSequenceGenericRawSequence[R, T](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing]
  )(implicit rowToObject: R => T, tc: TraceContext): Future[Seq[T]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGenericRawSequence")
    queryResult.map { res: Seq[R] =>
      res.map(rowToObject)
    }
  }

  def lookupSequenceGeneric[R, T](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing],
    count: Int
  )(implicit rowToObject: R => T, tc: TraceContext): Future[Result[Seq[T]]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGeneric")
    queryResult.map { res: Seq[R] =>
      Right(res.takeRight(count).map(rowToObject))
    }
  }

  def lookupSequenceGeneric[R, T](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing],
    from: Int,
    to: Int
  )(implicit rowToObject: R => T, tc: TraceContext): Future[Result[Seq[T]]] = {
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
  )(implicit rowToObject: R => T, tc: TraceContext): Future[Result[Seq[Z]]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq).trace("lookupSequenceGeneric")
    queryResult.map { res: Seq[R] =>
      Right(res.map(rowToObject).map(mapping).filter(filter).map(afterFilterMapping))
    }
  }

  def lookupSequenceGeneric[R, T, Z](
    querySeq: DBIOAction[Seq[R], NoStream, Nothing],
    sequenceMapping: Seq[T] => Z
  )(implicit rowToObject: R => T, tc: TraceContext): Future[Result[Z]] = {
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
  )(implicit rowToObject: R => T, tc: TraceContext): Future[Result[T]] = {
    val result = for {
      lookup        <- EitherT(db.run(queryById).map(_.toEither(BadRequest())).trace("updateGeneric lookup"))
      patchedObject <- EitherT(Future(Option(patch(rowToObject(lookup))).toEither(BadRequest())))
      patchResult   <-
        EitherT[Future, ResultStatus, T](
          db.run(update(patchedObject))
            .map { x =>
              if (x != 0) Right(patchedObject)
              else {
                tc.log.error("Database Result Updating entity")
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
  )(implicit rowToObject: R => T, objectToRow: T => R, tc: TraceContext): Future[Result[T]] = {
    val entityToSave = objectToRow(entity)
    val result       = for {
      _             <- db.run(queryById).map(_.toInverseEither(BadRequest())).trace("createGeneric lookup")
      createdObject <- db.run(create(entityToSave)).trace("createGeneric create")
      res           <- Future(
                         Right(rowToObject(createdObject))
                       )
    } yield res
    result
  }

  def createGeneric[R, T](
    entity: T,
    create: R => DBIOAction[R, NoStream, Effect.Write]
  )(implicit rowToObject: R => T, objectToRow: T => R, tc: TraceContext): Future[Result[T]] = {
    val entityToSave = objectToRow(entity)
    val result       = for {
      createdObject <- db.run(create(entityToSave)).trace("createGeneric create")
      res           <- Future(
                         Right(rowToObject(createdObject))
                       )
    } yield res
    result
  }

  def deleteGeneric[R, T](
    queryById: DBIOAction[Option[R], NoStream, Nothing],
    delete: DBIOAction[Int, NoStream, Effect.Write]
  )(implicit tc: TraceContext): Future[Result[Boolean]] = {
    val result = for {
      _              <- db.run(queryById).map(_.toEither(BadRequest())).trace("deleteGeneric lookup")
      dbDeleteResult <- db.run(delete)
                          .map { x =>
                            if (x != 0)
                              Right(true)
                            else {
                              tc.log.error("Database Error deleting entity")
                              Left(
                                DatabaseResult(
                                  "could not delete entity"
                                )
                              )
                            }
                          }
                          .trace("deleteGeneric delete")
    } yield dbDeleteResult
    result
  }

  /**
   * Close dao
   * @return
   */
  def close(): Future[Unit] =
    Future.successful(db.close())

}
