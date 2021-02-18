package de.innfactory.bootstrapplay2.common.filteroptions

import dbdata.Tables
import de.innfactory.play.slick.enhanced.utils.filteroptions.{
  BooleanOption,
  FilterOptions,
  LongOption,
  OptionStringOption
}

class FilterOptionsConfig {

  val companiesFilterOptions: Seq[FilterOptions[Tables.Company, _]] = Seq(
    OptionStringOption(v => v.stringAttribute1, "stringAttribute1"),
    OptionStringOption(v => v.stringAttribute2, "stringAttribute2"),
    LongOption(v => v.longAttribute1, "longAttribute1"),
    BooleanOption(v => v.booleanAttribute, "booleanAttribute")
  )

}
