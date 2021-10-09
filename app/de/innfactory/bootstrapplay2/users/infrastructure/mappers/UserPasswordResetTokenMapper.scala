package de.innfactory.bootstrapplay2.users.infrastructure.mappers

import dbdata.Tables
import de.innfactory.bootstrapplay2.users.domain.models.{ UserId, UserPasswordResetToken }
import io.scalaland.chimney.dsl.TransformerOps

object UserPasswordResetTokenMapper {

  implicit def entityToUserPasswordResetTokensRow(
    entity: UserPasswordResetToken
  ): Tables.UserPasswordResetTokensRow =
    entity.into[Tables.UserPasswordResetTokensRow].withFieldComputed(_.userId, u => u.userId.value).transform

  implicit def rowToUserPasswordResetTokensObject(
    row: Tables.UserPasswordResetTokensRow
  ): UserPasswordResetToken =
    row.into[UserPasswordResetToken].withFieldComputed(_.userId, u => UserId(u.userId)).transform
}
