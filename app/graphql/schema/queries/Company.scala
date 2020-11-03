package de.innfactory.familotel.cms.graphql.queries

import de.innfactory.familotel.cms.graphql.models.GraphQLExecutionContext
import de.innfactory.familotel.cms.graphql.models.arguments.Arguments.{ GroupID, GroupIDs }
import de.innfactory.familotel.cms.graphql.models.derivedTypes.GroupTypes.GroupType
import sangria.schema.{ Field, ListType }

object Group {
  val groupByGroupId: Field[GraphQLExecutionContext, Unit] = Field(
    "group",
    GroupType,
    arguments = GroupID :: Nil,
    resolve = ctx => ctx.ctx.groupRepository.lookupNoResult(ctx arg GroupID, ctx.ctx.request),
    description = Some("Familotel Filter API hotels query. Query group by id")
  )

  val groupsByIds: Field[GraphQLExecutionContext, Unit] = Field(
    "groups",
    ListType(GroupType),
    arguments = GroupIDs :: Nil,
    resolve = ctx => ctx.ctx.groupRepository.lookupSeqNoResult((ctx arg GroupIDs).map(_.toLong), ctx.ctx.request),
    description = Some("Familotel Filter API hotels query. Query all groups by id")
  )

  val allGroupsForUser: Field[GraphQLExecutionContext, Unit] = Field(
    "allGroupsForUser",
    ListType(GroupType),
    arguments = Nil,
    resolve = ctx => ctx.ctx.groupRepository.lookupAllForUser(ctx.ctx.request),
    description = Some("Familotel Filter API hotels query. Query all groups for user")
  )
}
