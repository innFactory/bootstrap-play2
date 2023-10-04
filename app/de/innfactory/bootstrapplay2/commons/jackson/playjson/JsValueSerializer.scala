package de.innfactory.bootstrapplay2.commons.jackson.playjson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import play.api.Logger
import play.api.libs.json.{JsValue, Json}

class JsValueSerializer extends StdSerializer[JsValue](classOf[JsValue]) {

  private val logger = Logger("play").logger

  override def serialize(value: JsValue, gen: JsonGenerator, provider: SerializerProvider): Unit = {
    logger.trace("[JsValueSerializer] serialize " + value.toString())
    gen.writeString(Json.prettyPrint(value))
  }

}
