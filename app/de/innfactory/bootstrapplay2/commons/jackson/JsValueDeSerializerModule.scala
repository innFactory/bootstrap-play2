package de.innfactory.bootstrapplay2.commons.jackson

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.module.SimpleModule
import de.innfactory.bootstrapplay2.commons.jackson.playjson.{JsValueDeserializer, JsValueSerializer}
import play.api.libs.json.JsValue

class JsValueDeSerializerModule() extends SimpleModule("JsValueDeSerializerModule", Version.unknownVersion()) {

  // first deserializers
  addDeserializer(classOf[JsValue], new JsValueDeserializer)
  addSerializer(classOf[JsValue], new JsValueSerializer)

}
