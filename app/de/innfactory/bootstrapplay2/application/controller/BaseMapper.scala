package de.innfactory.bootstrapplay2.application.controller

import de.innfactory.play.smithy4play.PlayJsonToDocumentMapper
import io.scalaland.chimney.Transformer
import org.joda.time.DateTime
import play.api.libs.json.JsValue
import smithy4s.Document
import de.innfactory.bootstrapplay2.definition.DateWithTime

import scala.language.implicitConversions

trait BaseMapper {

  implicit val transformJsValueToDocument: Transformer[JsValue, Document] = PlayJsonToDocumentMapper.mapToDocument
  implicit val transformDocumentToJsValue: Transformer[Document, JsValue] = PlayJsonToDocumentMapper.documentToJsValue

  implicit def unitMapper[T](any: T): Unit = ()

  implicit def dateWithTimeToDateTime(dateWithTime: DateWithTime): DateTime =
    DateTime.parse(dateWithTime.value)

  implicit def dateTimeToDateWithTime(dateTime: DateTime): DateWithTime =
    DateWithTime(dateTime.toString())

  implicit def optionMapper[T, R](option: Option[T])(implicit optionValueMapper: T => R): Option[R] =
    option.map(optionValueMapper)

  implicit def sequenceTransformer[T, R](seq: Seq[T])(implicit transform: T => R): List[R] =
    seq.map(transform).toList

  implicit val dateWithTimeToDateTimeTransformer: Transformer[DateWithTime, DateTime] =
    (dateWithTime: DateWithTime) => dateWithTimeToDateTime(dateWithTime)

  implicit val dateTimeToDateWithTimeTransformer: Transformer[DateTime, DateWithTime] =
    (dateTime: DateTime) => dateTimeToDateWithTime(dateTime)
}
