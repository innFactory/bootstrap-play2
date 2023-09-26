$version: "2.0"

namespace de.innfactory.bootstrapplay2.api
use alloy#uuidFormat

@pattern("^\\d{4}-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d(\\.\\d+)?(([+-]\\d\\d:\\d\\d)|Z)?$")
@documentation("ISO Date With Time")
string DateWithTime

string CompanyId
string LocationId
