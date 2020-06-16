package common.daos

import java.util.UUID

import common.utils.OptionUtils._
import cats.data.EitherT
import cats.implicits._
import com.vividsolutions.jts.geom.Geometry
import common.GeoPointFactory.GeoPointFactory
import common.results.Results.{ ErrorStatus, Result }
import common.results.errors.Errors.{ BadRequest, DatabaseError, NotFound }
import db.codegen.XPostgresProfile
import javax.inject.{ Inject, Singleton }
import slick.jdbc.JdbcBackend.Database
import play.api.libs.json.Json
import models.api.{ ApiBaseModel, Location => LocationObject }
import dbdata.Tables
import org.joda.time.DateTime
import slick.basic.BasicStreamingAction
import slick.lifted.{ CompiledFunction, Query, Rep, TableQuery }

import scala.reflect.runtime.{ universe => ru }
import ru._
import scala.concurrent.{ ExecutionContext, Future }
import scala.language.implicitConversions
import scala.concurrent.{ ExecutionContext, Future }

class BaseSlickDAO(db: Database)(implicit ec: ExecutionContext) extends Tables {

  val currentClassForDatabaseError = "BaseSlickDAO"

  override val profile = XPostgresProfile

  import profile.api._

  def lookupGeneric[R, T](rowToObject: R => T,
                          queryHeadOption: DBIOAction[Option[R], NoStream, Nothing]): Future[Result[T]] = {
    val queryResult: Future[Option[R]] = db.run(queryHeadOption)
    queryResult.map { res: Option[R] =>
      if (res.isDefined)
        Right(rowToObject(res.get))
      else
        Left(
          NotFound()
        )
    }
  }

  def lookupSequenceGeneric[R, T](rowToObject: R => T,
                                  querySeq: DBIOAction[Seq[R], NoStream, Nothing]): Future[Result[Seq[T]]] = {
    val queryResult: Future[Seq[R]] = db.run(querySeq)
    queryResult.map { res: Seq[R] =>
      Right(res.map(rowToObject))
    }
  }

  def updateGeneric[R, T](rowToObject: R => T,
                          queryById: DBIOAction[Option[R], NoStream, Nothing],
                          update: T => DBIOAction[Int, NoStream, Effect.Write],
                          patch: T => T): Future[Result[T]] = {
    val result = for {
      lookup        <- EitherT(db.run(queryById).map(_.toEither(BadRequest())))
      patchedObject <- EitherT(Future(Option(patch(rowToObject(lookup))).toEither(BadRequest())))
      patchResult <- EitherT[Future, ErrorStatus, T](
                      db.run(update(patchedObject))
                        .map(x => {
                          if (x != 0) Right(patchedObject)
                          else Left(DatabaseError("Could not replace entity", currentClassForDatabaseError, "update", "row not updated"))
                        })
                    )
    } yield patchResult
    result.value
  }

  def createGeneric[R, T](
    entity: T,
    rowToObject: R => T,
    objectToRow: T => R,
    queryById: DBIOAction[Option[R], NoStream, Nothing],
    create: R => DBIOAction[R, NoStream, Effect.Write],
  ): Future[Result[T]] = {
    val entityToSave = objectToRow(entity)
    val result = for {
      _             <- db.run(queryById).map(_.toInverseEither(BadRequest()))
      createdObject <- db.run(create(entityToSave))
      res <- Future(
              Option(rowToObject(createdObject))
                .toEither(DatabaseError("Could not create entity", currentClassForDatabaseError, "create", "row not created"))
            )
    } yield res
    result
  }

  def deleteGeneric[R, T](
    queryById: DBIOAction[Option[R], NoStream, Nothing],
    delete: DBIOAction[Int, NoStream, Effect.Write],
  ): Future[Result[Boolean]] = {
    val result = for {
      _ <- db.run(queryById).map(_.toEither(BadRequest()))
      dbDeleteResult <- (db.run(delete).map { x =>
                         if (x != 0)
                           Right(true)
                         else
                           Left(DatabaseError("could not delete entity", currentClassForDatabaseError, "delete", "entity was deleted"))
                       })
    } yield dbDeleteResult
    result
  }

  /**
   * Close db
   * @return
   */
  def close(): Future[Unit] =
    Future.successful(db.close())

}
