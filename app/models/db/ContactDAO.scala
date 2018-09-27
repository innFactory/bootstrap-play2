package models.db

import common.messages.SlickIO
import common.messages.SlickIO.{Result, SuccessWithStatusCode, _}
import db.codegen.XPostgresProfile
import javax.inject.{Inject, Singleton}
import models.api.{Contact}
import org.joda.time.DateTime
import slick.jdbc.JdbcBackend.Database
import dbdata.Tables
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

/**
  * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
  */
trait ContactDAO {

  def lookup(id: Long, showDeleted: Boolean): Future[Result[Any]]

  def getCount(showDeleted: Boolean, lastName: Option[String]): Future[Int]

  def all(from: Int, to: Int, showDeleted: Boolean, lastName: Option[String]): Future[Result[SuccessWithStatusCode[Seq[Contact]]]]

  def create(contact: Contact): Future[Result[SuccessWithStatusCode[Contact]]]

  def replace(contact: Contact): Future[Result[SuccessWithStatusCode[Boolean]]]

  def update(contact: Contact): Future[Result[SuccessWithStatusCode[Boolean]]]

  def delete(id: Long): Future[Result[SuccessWithStatusCode[Boolean]]]

  def close(): Future[Unit]
}

/**
  * A Contact DAO implemented with Slick, leveraging Slick code gen.
  *
  * Note that you must run "flyway/flywayMigrate" before "compile" here.
  *
  * @param db the slick database that this contact DAO is using internally, bound through Module.
  * @param ec a CPU bound execution context.  Slick manages blocking JDBC calls with its
  *    own internal thread pool, so Play's default execution context is fine here.
  */
@Singleton
class SlickContactDAO @Inject()(db: Database)(implicit ec: ExecutionContext) extends ContactDAO with Tables {

  override val profile = XPostgresProfile

  import profile.api._

  private val queryById = Compiled(
    (id: Rep[Long]) => Contacts.filter(_.id === id))

  def lookup(id: Long, showDeleted: Boolean): Future[Result[SuccessWithStatusCode[Contact]]] = {
    val f: Future[Option[ContactsRow]] = db.run(queryById(id).result.headOption)
    f.map {
      case Some(row) => if(showDeleted || !row.deleted) {
        Right(SlickIO.SuccessWithStatusCode(contactsRowToContacts(row), 200))
      } else {
        Left(DatabaseError("Entity not Found", 404))
      }
      case None => Left(DatabaseError("Entity not Found", 404))
    }
  }

  def getCount(showDeleted: Boolean, lastName: Option[String]): Future[Int] = {
    var l = db.run(Contacts.result)
       l.map(s => {
         val newSeq = lastName match {
           case Some(name: String) => s.filter(obj => {
             obj.lastName.get == name
           })
           case None => s
         }
         if (showDeleted) {
           newSeq.length
         } else {
           newSeq.filterNot(_.deleted).length
         }
        }

       )
  }

  def all(from: Int, to: Int, showDeleted: Boolean, lastName: Option[String]): Future[Result[SuccessWithStatusCode[Seq[Contact]]]] = {
    val f = db.run(Contacts.result)
    f.map( seq => {
      val newSeq = lastName match {
        case Some(name: String) => seq.filter( obj => {
          obj.lastName.get == name
        })
        case None => seq
      }
      if(showDeleted) {
        Right(SuccessWithStatusCode(newSeq.slice(from, to + 1).map(contactsRowToContacts), 200))
      } else {
        Right(SuccessWithStatusCode(newSeq.filterNot(_.deleted).slice(from, to + 1).map(contactsRowToContacts), 200))
      } 
    }
    )
  }

    def update(contact: Contact): Future[Result[SuccessWithStatusCode[Boolean]]] = {
      (db.run(queryById(contact.id.getOrElse(0)).result.headOption)).map {
          case Some(option) => {
            val oldContact = contactsRowToContacts(option)
            val newContact = contact.copy(
              id = contact.id,

              firstName = contact.firstName match {
                case Some(of) => Some(of)
                case None => oldContact.firstName
              },

              lastName = contact.lastName match {
                case Some(of) => Some(of)
                case None => oldContact.lastName
              },

              zip = contact.zip match {
                case Some(of) => Some(of)
                case None => oldContact.zip
              },

              city = contact.city match {
                case Some(of) => Some(of)
                case None => oldContact.city
              },

              street =  contact.street match {
                case Some(of) => Some(of)
                case None => oldContact.street
              },
              street2 = contact.street2 match {
                case Some(of) => Some(of)
                case None => oldContact.street2
              },
             email = contact.email match {
                case Some(of) => Some(of)
                case None => oldContact.email
              },

              createdBy = contact.createdBy match {
                case Some(of) => Some(of)
                case None => oldContact.createdBy
              },

              createdDate = contact.createdDate match {
                case Some(of) => Some(of)
                case None => oldContact.createdDate
              },

              changedBy = contact.changedBy,

              changedDate = Some(DateTime.now()),

              deleted = contact.deleted match {
                case Some(of) => Some(of)
                case None => oldContact.deleted
              }
            )

            db.run(queryById(contact.id.getOrElse(0)).update(contactsToContactsRow(newContact))).map {
              case 0 => Left(DatabaseError("Could not replace entity", 500))
              case _ => Right(SuccessWithStatusCode(true, 204))
            }
          }
          case None => Future(Left(DatabaseError("Could not find entity to replace", 404)))
        }.flatten
    }

  def replace(contact: Contact): Future[Result[SuccessWithStatusCode[Boolean]]] = {
    val f: Future[Option[ContactsRow]] =  db.run(queryById(contact.id.getOrElse(0)).result.headOption)
    f.map{
        case Some(option) => {
          val oldContact = contactsRowToContacts(option)
          val newContact = contact.copy(
            id = contact.id,
            firstName = contact.firstName,
            lastName = contact.lastName,
            zip = contact.zip ,
            city = contact.city,
            street =  contact.street,
            street2 = contact.street2,
            email = contact.email ,
            createdBy = oldContact.createdBy,
            createdDate = oldContact.createdDate,

            changedBy = contact.changedBy,
            changedDate = Some(DateTime.now()),

            deleted = Some(contact.deleted.getOrElse(false))
          )
          db.run(queryById(contact.id.getOrElse(0)).update(contactsToContactsRow(newContact))).map {
            case 0 => Left(DatabaseError("Could not update entity", 500))
            case _ => Right(SuccessWithStatusCode(true, 204))
          }
        }
        case None => Future(Left(DatabaseError("Could not find entity to update", 404)))
      }.flatMap(s => s)

  }


    def delete(id: Long): Future[Result[SuccessWithStatusCode[Boolean]]] = {
        db.run(queryById(id).result.headOption).map {
          case Some(entity) => {
            if (entity.deleted) {
              Future(Left(DatabaseError("could not delete entity", 500)))
            } else {
              db.run(queryById(id).update(entity.copy(deleted = true))).map {
                case 0 => Left(DatabaseError("could not delete entity", 500))
                case _ => Right(SuccessWithStatusCode(true, 204))
              }
            }
          }
          case None => Future(Left(DatabaseError("entity not found", 404)))
        }.flatten
    }

    def create(contact: Contact): Future[Result[SuccessWithStatusCode[Contact]]] = {
      val contactToSave = contactsToContactsRow(contact.copy(createdDate = Some(DateTime.now())))
      val action = (Contacts returning Contacts.map(_.id)) += contactToSave
      val successID = db.run(action)
      successID.map {
        case 0 => Future(Left(DatabaseError("failed to create", 500)))
        case long => {
          val idToQuery = long
          val createdContact = db.run(queryById(idToQuery).result.headOption)
          val newContact = createdContact.map {
            case Some(row) => Right(SuccessWithStatusCode(contactsRowToContacts(row), 200))
            case None => Left(DatabaseError("failed to return created entity", 500))
          }
          newContact
        }
      }.flatten
    }

  def close(): Future[Unit] = {
    Future.successful(db.close())
  }

  private def contactsToContactsRow(contact: Contact): ContactsRow = {
    ContactsRow(
      contact.id.getOrElse(0),

      contact.firstName,

      contact.lastName,

      contact.zip,

      contact.city,

      contact.street,

      contact.street2,

      contact.email,

      contact.createdBy,

      contact.createdDate,

      contact.changedBy,

      contact.changedDate,

      contact.deleted.getOrElse(false)
    )
  }

  private def contactsRowToContacts(contactsRow: ContactsRow): Contact = {
    Contact(
      Some(contactsRow.id),

      contactsRow.firstName,

      contactsRow.lastName,

      contactsRow.zip,

      contactsRow.city,

      contactsRow.street,

      contactsRow.street2,

      contactsRow.email,

      contactsRow.createdBy,

      contactsRow.createdDate,

      contactsRow.changedBy,

      contactsRow.changedDate,

      None
    )
  }
}



