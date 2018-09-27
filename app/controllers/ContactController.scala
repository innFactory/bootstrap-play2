package controllers

import akka.http.scaladsl.model.HttpResponse
import common.messages.SlickIO.{DatabaseError, SuccessWithStatusCode}
import io.swagger.annotations.{ApiResponse, ApiResponses}
import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.libs.json.Json.toJson
import play.api.mvc._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.libs.json.Reads
import play.api.libs.json._
import io.swagger.annotations._
import io.swagger.core._
import io.swagger.annotations.ApiKeyAuthDefinition.ApiKeyLocation
import models.api._
import models.api.PagedGen._
import models.db.{ContactDAO}
import models.api.Contact
import models.api.Contact.contactWriter
import models.api.Contact.contactReads
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.libs.json.Reads

import scala.concurrent.ExecutionContext
@Singleton @Api(value = "Contacts")
class ContactController @Inject()(cc: ControllerComponents, contactsDAO:  ContactDAO)
                                  (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  implicit val nilReader = Json.reads[scala.collection.immutable.Nil.type]
  implicit val nilWriter = Json.writes[scala.collection.immutable.Nil.type]
  implicit val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

  @ApiOperation(
    nickname = "getContactsPage",
    value = "Get Page of Contacts Contained in PageData Object",
    notes = "Returns a PagedDataObject including an array of Contacts, a prev and next link for paging and the complete Contacts count",
    response = classOf[PagedContactData],
    httpMethod = "GET",
    produces = "application/json",
    code=200
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message= "Ok", reference = "PagedContactData"),
    new ApiResponse(code = 500, message = "Internal Server Error")))
  def contacts(@ApiParam(name = "lastName",value="query for last Name", required = false )lastName: Option[String], @ApiParam(name = "startIndex",value="Index of first contact of page", required = false )startIndex: Option[Int], @ApiParam(name = "endIndex",value="Index of last contact of page", required = false )endIndex: Option[Int], @ApiParam(name = "showDeleted",value="Show deleted Contacts too", required = false ) showDeleted: Option[Boolean]) = Action.async { implicit request =>
    contactsDAO.getCount(showDeleted.getOrElse(false), lastName).map { i =>
      val from = startIndex.getOrElse(0)
      var to = endIndex.getOrElse(250)
      if(to - from > 250) {
        to = from + 250
      }
      if(to > i) {
        to = i - 1
      }
      contactsDAO.all(from, to, showDeleted.getOrElse(false), lastName).map {
        case Right(succ: SuccessWithStatusCode[Seq[Contact]]) => {
          val body = succ.body.map {
            contact => {
              Json.toJson(contact)
            }
          }
          val pagedData = new PagedData[JsValue](
            data = Json.toJson(body),
            prev = PagedGen.prevGen(to, from, i, "/v1/contact"),
            next = PagedGen.nextGen(to, from, i, "/v1/contact"),
            count = i
          )
          Status(succ.status)(Json.toJson(pagedData))
        }
        case Left(err: DatabaseError) => Status(err.statusCode)(err.message)
        case _ => Status(500)("Internal Server Error")
      }
    }.flatten
  }

  @ApiOperation(
    nickname = "getContactById",
    value = "Returns contact based on given id if found",
    notes = "Id has to be provided",
    response = classOf[Contact],
    httpMethod = "GET",
    produces = "application/json",
    code=200
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message= "OK", reference = "Contact", responseContainer = "JSON"),
    new ApiResponse(code = 404, message= "Id not found"),
    new ApiResponse(code = 500, message = "Internal Server Error")))
  def getContact(@ApiParam(name = "id",value="Long", required = true )id: Long, @ApiParam(name = "showDeleted",value="Show contact if it is deleted", required = false )showDeleted: Option[Boolean]) = Action.async { implicit request =>
    contactsDAO.lookup(id, showDeleted.getOrElse(false)).map {
      case Left(err: DatabaseError) => Status(err.statusCode)(err.message)
      case Right(succ: SuccessWithStatusCode[Contact]) => Status(succ.status)(Json.toJson(succ.body))
      case _ => Status(500)("Internal Server Error")
    }
  }

  @ApiOperation(
    nickname = "createContact",
    value = "Creates new Contact from Json Body",
    notes = "Body must be provided and ID is auto generated. Returns created Object as Json",
    response = classOf[Contact],
    httpMethod = "POST",
    consumes = "application/json",
    produces = "application/json",
    code=200
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message= "Created", reference = "Contact", responseContainer = "JSON"),
    new ApiResponse(code = 500, message = "Internal Server Error")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Object", value = "models.api.Contact", required = true, dataType = "models.api.Contact", paramType = "body")
  ))
  def postContact = Action.async { implicit request =>
      val json = request.body.asJson
      val stock = json.get.as[Contact]
      contactsDAO.create(stock).map {
        case Left(err: DatabaseError) => Status(err.statusCode)(err.message)
        case Right(succ: SuccessWithStatusCode[Contact]) => Status(succ.status)(Json.toJson(succ.body))
        case _ => Status(500)("Internal Server Error")
      }
  }

  @ApiOperation(
    nickname = "deleteContact",
    value = "Soft deletes Contact",
    notes = "Id has to be provided",
    response = classOf[Contact],
    httpMethod = "DELETE",
    code=201
  )
  @ApiResponses(Array(
    new ApiResponse(code = 404, message= "Id not found"),
    new ApiResponse(code = 500, message = "Internal Server Error")))
  def deleteContact(@ApiParam(name = "id",value="Long", required = true )id: Long) = Action.async { implicit request =>
    contactsDAO.delete(id.toLong).map {
      case Left(err: DatabaseError) => Status(err.statusCode)(err.message)
      case Right(succ: SuccessWithStatusCode[Boolean]) => Status(succ.status)
      case _ => Status(500)("Internal Server Error")
    }
  }

  @ApiOperation(
    nickname = "patchContact",
    value = "Updates Contact",
    notes = "Only Id is required. Only set fields will be updated",
    response = classOf[Contact],
    httpMethod = "PATCH",
    consumes = "application/json",
    produces = "application/json",
    code=201
  )
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal Server Error")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Object", value = "Object to be updated", required = true, dataType = "models.api.Contact", paramType = "body")
  ))
  def patchContact = Action.async { implicit request =>
    val json = request.body.asJson
    val stock = json.get.as[Contact]
    contactsDAO.update(stock).map {
      case Left(err: DatabaseError) => Status(err.statusCode)(err.message)
      case Right(succ: SuccessWithStatusCode[Boolean]) => Status(succ.status)
      case _ => Status(500)("Internal Server Error")

    }
  }
  @ApiOperation(
    nickname = "replaceContact",
    value = "Replaces Contact",
    notes = "Only Id is required. New Object contains only the fields in request body ",
    response = classOf[Contact],
    httpMethod = "PUT",
    consumes = "application/json",
    produces = "application/json",
    code=201
  )
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal Server Error")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Object", value = "Object to be replaced", required = true, dataType = "models.api.Contact", paramType = "body")
  ))
  def putContact = Action.async { implicit request =>
    val json = request.body.asJson
    val stock = json.get.as[Contact]
    contactsDAO.replace(stock).map {
        case Left(err: DatabaseError) => Status(err.statusCode)(err.message)
        case Right(succ: SuccessWithStatusCode[Boolean]) => Status(succ.status)
        case _ => Status(500)("Internal Server Error")
    }
  }

}
