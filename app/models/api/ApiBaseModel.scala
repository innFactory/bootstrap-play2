package models.api

import play.api.libs.json.JsValue

trait ApiBaseModel {
  def toJson: JsValue
}
