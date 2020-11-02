package db.codegen
import de.innfactory.play.db.codegen.XPostgresProfile
import de.innfactory.play.db.codegen.{ Config, CustomizedCodeGeneratorBase, CustomizedCodeGeneratorConfig }

class CodeGenConfig() extends Config[XPostgresProfile] {
  override lazy val slickProfile: XPostgresProfile = XPostgresProfile
}

object CustomizedCodeGenerator
    extends CustomizedCodeGeneratorBase(
      CustomizedCodeGeneratorConfig(profile = "de.innfactory.play.db.codegen.XPostgresProfile"),
      new CodeGenConfig()
    ) {

  override def included: Seq[String] = Seq("COMPANY", "LOCATION")
}
