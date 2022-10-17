package de.innfactory.bootstrapplay2.commons.jackson.playjson

import com.fasterxml.jackson.core.{JsonParser, JsonTokenId}
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import play.api.Logger
import play.api.libs.json.{JsNull, JsValue, Json}

class JsValueDeserializer extends StdDeserializer[JsValue](classOf[JsValue]) {

  private val logger = Logger("play").logger

  override def deserialize(p: JsonParser, ctxt: DeserializationContext): JsValue = {
    logger.trace("[JsValueDeserializer] deserialize " + p.getText)

    p.currentTokenId() match {
      case JsonTokenId.ID_STRING => Json.parse(p.getText)
      case _                     => JsNull
    }
  }
}
