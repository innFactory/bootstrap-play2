package de.innfactory.bootstrapplay2.commons.implicits

trait Showable { this: Product =>

  def show: String = {
    val className = this.productPrefix
    val fieldNames = this.productElementNames.toList
    val fieldValues = this.productIterator.toList
    val fields = fieldNames.zip(fieldValues).map { case (name, value) =>
      s"$name = $value"
    } // TODO match value -> if (product) call show else toString
    fields.mkString(s"$className(", ", ", ")")
  }
  override def toString: String = show
}
