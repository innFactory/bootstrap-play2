$version: "1.0"

namespace de.innfactory.bootstrapplay2.apidefinition

@pattern("^\\d{4}-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d(\\.\\d+)?(([+-]\\d\\d:\\d\\d)|Z)?$")
@documentation("ISO Date With Time")
string DateWithTime
