package db.codegen
import de.innfactory.play.db.codegen.XPostgresProfile
import de.innfactory.play.db.codegen.{ Config, CustomizedCodeGeneratorBase, CustomizedCodeGeneratorConfig }

class CodeGenConfig() extends Config[XPostgresProfile] {
  override lazy val slickProfile: XPostgresProfile = XPostgresProfile
}

object CustomizedCodeGenerator
    extends CustomizedCodeGeneratorBase(
      CustomizedCodeGeneratorConfig(),
      new CodeGenConfig()
    ) {

  // Update here if new Tables are added
  // Each Database Table, which should be included in CodeGen
  // has to be added here in UPPER-CASE
  override def included: Seq[String] = Seq("COMPANY", "LOCATION")
}
