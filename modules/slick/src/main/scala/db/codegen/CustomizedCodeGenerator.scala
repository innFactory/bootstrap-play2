package db.codegen

import de.innfactory.play.db.codegen.{Config, CustomizedCodeGeneratorBase, CustomizedCodeGeneratorConfig}

class CodeGenConfig() extends Config

class CustomizedCodeGenerator extends CustomizedCodeGeneratorBase(
CustomizedCodeGeneratorConfig(),
  new CodeGenConfig(),
) {
  override def included: Seq[String] = Seq("")
}
