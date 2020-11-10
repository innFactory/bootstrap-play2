package de.innfactory.bootstrapplay2.common.utils

import play.api.libs.json.{ Json, Reads, Writes }

object NilUtils {
  implicit val nilReader: Reads[Nil.type]  = Json.reads[scala.collection.immutable.Nil.type]
  implicit val nilWriter: Writes[Nil.type] = Json.writes[scala.collection.immutable.Nil.type]
}
