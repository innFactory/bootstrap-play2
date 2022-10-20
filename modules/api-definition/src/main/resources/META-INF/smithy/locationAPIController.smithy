$version: "2.0"

namespace de.innfactory.bootstrapplay2.api

use de.innfactory.bootstrapplay2.api#LocationId
use de.innfactory.bootstrapplay2.api#CompanyId
use smithy4s.api#simpleRestJson

@simpleRestJson
@httpBearerAuth
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

apply getAllLocationsByCompany @examples([
    {
        title: "Invoke getAllLocationsByCompany",
        input: {
            companyId: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3"
        },
        output: {
            body: [
                {
                    id: "592c5187-cb85-4b66-b0fc-293989923e1e",
                    company: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
                    name: "Location-1",
                    settings: {"location": "location"},
                    addressLine1: "location_1_address_line_1",
                    addressLine2: "location_1_address_line_2",
                    zip: "zip1",
                    city: "city1",
                    country: "country1",
                    created: "2022-03-07T00:00:00.001Z",
                    updated: "2022-03-07T00:00:00.001Z"
                }
            ]
        }
    }
])

@http(method: "GET", uri: "/v1/locations", code: 200)
@readonly
operation getAllLocations {
    output: LocationsResponse,
}

apply getAllLocations @examples([
    {
        title: "Invoke getAllLocations",
        output: {
            body: [
                {
                    id: "592c5187-cb85-4b66-b0fc-293989923e1e",
                    company: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
                    name: "Location-1",
                    settings: {"location": "location"},
                    addressLine1: "location_1_address_line_1",
                    addressLine2: "location_1_address_line_2",
                    zip: "zip1",
                    city: "city1",
                    country: "country1",
                    created: "2022-03-07T00:00:00.001Z",
                    updated: "2022-03-07T00:00:00.001Z"
                }
            ]
        }
    }
])

@http(method: "GET", uri: "/v1/locations/{locationId}", code: 200)
@readonly
operation getLocationById {
    input: LocationIdRequest,
    output: LocationResponse,
}

apply getLocationById @examples([
    {
        title: "Invoke getLocationById",
        input: {
            locationId: "592c5187-cb85-4b66-b0fc-293989923e1e"
        },
        output: {
            id: "592c5187-cb85-4b66-b0fc-293989923e1e",
            company: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
            name: "Location-1",
            settings: {"location": "location"},
            addressLine1: "location_1_address_line_1",
            addressLine2: "location_1_address_line_2",
            zip: "zip1",
            city: "city1",
            country: "country1",
            created: "2022-03-07T00:00:00.001Z",
            updated: "2022-03-07T00:00:00.001Z"
        }
    }
])

@http(method: "POST", uri: "/v1/locations", code: 200)
operation createLocation {
    input: LocationRequest,
    output: LocationResponse,
}

apply createLocation @examples([
    {
        title: "Invoke createLocation",
        input: {
            body: {
                company: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
                name: "test"
            }
        },
        output: {
            id: "0ce84627-9a66-46bf-1234-4f38b82a38e3",
            company: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
            name: "test",
            created: "2022-03-07T00:00:00.001Z",
            updated: "2022-03-07T00:00:00.001Z"
        }
    }
])

@http(method: "PATCH", uri: "/v1/locations", code: 200)
operation updateLocation {
    input: LocationRequest,
    output: LocationResponse,
}

apply updateLocation @examples([
    {
        title: "Invoke updateLocation",
        input: {
            body: {
                id: "592c5187-cb85-4b66-b0fc-293989923e1e",
                company: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
                name: "test2"
            }
        },
        output: {
            id: "592c5187-cb85-4b66-b0fc-293989923e1e",
            company: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
            name: "test2",
            created: "2022-03-07T00:00:00.001Z",
            updated: "2022-03-07T00:00:00.001Z"
        }
    }
])

@http(method: "DELETE", uri: "/v1/locations/{locationId}", code: 204)
@idempotent
operation deleteLocation {
    input: LocationIdRequest,
}

apply deleteLocation @examples([
    {
        title: "Invoke deleteLocation",
        input: {
            locationId: "592c5187-cb85-4b66-b0fc-293989923e1e"
        }
    }
])

// --------- REQUESTS -------------

structure GetLocationsByCompanyRequest {
    @httpLabel
    @required
    companyId: CompanyId
}

structure LocationIdRequest {
    @httpLabel
    @required
    locationId: LocationId
}

structure LocationRequest {
    @httpPayload
    @required
    body: LocationRequestBody
}

structure LocationRequestBody {
    id: LocationId,
    @required
    company: CompanyId,
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
    id: LocationId,
    @required
    company: CompanyId,
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