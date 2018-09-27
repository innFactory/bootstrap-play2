package models.api
import io.swagger.annotations._


@ApiModel
case class PagedContactData(
                              @ApiModelProperty(name = "data", dataType = "List[models.api.Contact]", value = "List of Contacts")
                              data: Contact,
                              @ApiModelProperty(name = "prev", dataType = "String", value = "Api endpoint for previous Page")
                              prev: String,
                              @ApiModelProperty(name = "next", dataType = "String", value = "Api endpoint for next Page")
                              next: String,
                              @ApiModelProperty(name = "count", dataType = "Long", value = "Number of all aviable Contacts")
                              count: Long
                            )
