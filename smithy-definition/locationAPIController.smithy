$version: "1.0"

namespace de.innfactory.bootstrapplay2.definition

use smithy4s.api#simpleRestJson

@simpleRestJson
service LocationAPIController {
    version: "1.0.0",
    operations: [
        getAllLocationsByCompany, getAllLocations, getLocationById, createLocation, updateLocation, deleteLocation
    ]
}

// --------- OPERATIONS -------------

@http(method: "GET", uri: "/v1/companies/{companyId}/locations", code: 200)
@readonly
operation getAllLocationsByCompany {
    input: GetLocationsByCompanyRequest,
    output: LocationsResponse,
}

@http(method: "GET", uri: "/v1/locations", code: 200)
@readonly
operation getAllLocations {
    output: LocationsResponse,
}

@http(method: "GET", uri: "/v1/locations/{locationId}", code: 200)
@readonly
operation getLocationById {
    input: LocationIdRequest,
    output: LocationResponse,
}

@http(method: "POST", uri: "/v1/locations", code: 200)
@readonly
operation createLocation {
    input: LocationRequest,
    output: LocationResponse,
}

@http(method: "PATCH", uri: "/v1/locations", code: 200)
@readonly
operation updateLocation {
    input: LocationRequest,
    output: LocationResponse,
}

@http(method: "DELETE", uri: "/v1/locations/{locationId}", code: 200)
@readonly
operation deleteLocation {
    input: LocationIdRequest,
}

// --------- REQUESTS -------------

structure GetLocationsByCompanyRequest {
    @httpLabel
    @required
    companyId: Long
}

structure LocationIdRequest {
    @httpLabel
    @required
    locationId: Long
}

structure LocationRequest {
    @httpPayload
    @required
    body: LocationRequestBody
}

structure LocationRequestBody {
    id: Long,
    @required
    company: Long,
    name: String,
    settings: Document,
    addressLine1: String,
    addressLine2: String,
    zip: String,
    city: String,
    country: String
}

// --------- RESPONSES -------------

structure LocationResponse {
    @required
    id: Long,
    @required
    company: Long,
    name: String,
    settings: Document,
    addressLine1: String,
    addressLine2: String,
    zip: String,
    city: String,
    country: String,
    @required
    created: DateWithTime,
    @required
    updated: DateWithTime
}

structure LocationsResponse {
    @httpPayload
    @required
    body: LocationsResponseList
}

list LocationsResponseList {
    member: LocationResponse
}