package de.innfactory.bootstrapplay2.users.infrastructure.mappers

import com.google.firebase.auth.UserRecord
import de.innfactory.bootstrapplay2.users.domain.models.{ Claims, User, UserId }
import play.api.Logger
import play.api.libs.json.{ JsValue, Json }

import scala.collection.JavaConverters._

object ClaimMapper {
  def claimsToMap(claims: Claims): Map[String, JsValue] = {
    val claimSeq: Seq[(String, JsValue)] = Seq(
      ("innFactoryAdmin", claims.innFactoryAdmin.map(b => Json.toJson(b))),
      ("companyAdmin", claims.companyAdmin.map(l => Json.toJson(l)))
    ).filter(_._2.isDefined).map(t => (t._1, t._2.get))
    Map.from(claimSeq)
  }
}
