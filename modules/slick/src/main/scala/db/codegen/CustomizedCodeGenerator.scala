package db.codegen

import db.codegen.Config.{ jdbcDriver, slickProfile, url }
import slick.sql.SqlProfile.ColumnOption

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 *  This customizes the Slick code generator. We only do simple name mappings.
 *  For a more advanced example see https://github.com/cvogt/slick-presentation/tree/scala-exchange-2013
 */
object CustomizedCodeGenerator {
  import scala.concurrent.ExecutionContext.Implicits.global

  val projectDir = System.getProperty("user.dir")

  def main(args: Array[String]): Unit =
    // write the generated results to file
    Await.result(
      codegen.map(
        _.writeToFile(
          "db.codegen.XPostgresProfile", // use our customized postgres driver
          s"$projectDir/target/scala-2.12/src_managed/slick",
          "dbdata",
          "Tables",
          "Tables.scala"
        )
      ),
      20.seconds
    )

  val db = slickProfile.api.Database.forURL(url, driver = jdbcDriver)

  // filter out desired tables
  //val included = Seq("COFFEE","SUPPLIER","COFFEE_INVENTORY")
  lazy val codegen = db.run {
    slickProfile.defaultTables //.map(_.filter(t => included contains t.name.name.toUpperCase))
      .flatMap(
        slickProfile
          .createModelBuilder(_, ignoreInvalidDefaults = false)
          .buildModel
      )
  }.map { model =>
    new slick.codegen.SourceCodeGenerator(model) {
      override def Table =
        new Table(_) { table =>
          override def Column =
            new Column(_) { column =>
              // customize db type -> scala type mapping, pls adjust it according to your environment
              override def rawType: String =
                model.options
                  .find(_.isInstanceOf[ColumnOption.SqlType])
                  .flatMap { tpe =>
                    tpe.asInstanceOf[ColumnOption.SqlType].typeName match {
                      case "hstore"                                      => Option("Map[String, String]")
                      case "_text" | "text[]" | "_varchar" | "varchar[]" =>
                        Option("List[String]")
                      case "geometry"                                    =>
                        Option("com.vividsolutions.jts.geom.Geometry")
                      case "_int8" | "int8[]"                            => Option("List[Long]")
                      case "_int4" | "int4[]"                            => Option("List[Int]")
                      case "_int2" | "int2[]"                            => Option("List[Short]")
                      case _                                             => None
                    }
                  }
                  .getOrElse {
                    model.tpe match {
                      case "java.sql.Date"      => "org.joda.time.LocalDate"
                      case "java.sql.Time"      => "org.joda.time.LocalTime"
                      case "java.sql.Timestamp" => "org.joda.time.DateTime"
                      case _                    =>
                        super.rawType

                    }
                  }
            }
        }

      // ensure to use our customized postgres driver at `import profile.simple._`
      override def packageCode(profile: String, pkg: String, container: String, parentType: Option[String]): String =
        s"""
package ${pkg}
import com.github.tototoshi.slick.PostgresJodaSupport._
import org.joda.time.DateTime
import com.vividsolutions.jts.geom.Point
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object ${container} extends {
  val profile = ${profile}
} with ${container}
/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait ${container}${parentType.map(t => s" extends $t").getOrElse("")} {
  val profile: $profile
  import profile.api._
  ${indent(code)}
}
      """.trim()
    }
  }
}
