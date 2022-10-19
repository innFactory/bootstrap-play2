$version: "2.0"

namespace de.innfactory.bootstrapplay2.api

use de.innfactory.bootstrapplay2.api#CompanyId
use smithy4s.api#simpleRestJson

@simpleRestJson
@httpBearerAuth
service CompanyAPIController {
    version: "1.0.0",
    operations: [
        getCompanyById, getAllCompanies, createCompany, updateCompany, deleteCompany
    ]
}

// --------- OPERATIONS -------------

@http(method: "GET", uri: "/v1/companies/{companyId}", code: 200)
@readonly
operation getCompanyById {
    input: CompanyIdRequest,
    output: CompanyResponse,
}

apply getCompanyById @examples([
    {
        title: "Invoke getCompanyById",
        input: {
            companyId: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3"
        },
        output: {
            id: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
            settings: {"region": "region"},
            stringAttribute1: "test 1",
            stringAttribute2: "test 2",
            longAttribute1: 1,
            booleanAttribute: false,
            created: "2022-03-07T00:00:00.001Z",
            updated: "2022-03-07T00:00:00.001Z"
        }
    }
])

@http(method: "GET", uri: "/v1/companies", code: 200)
@readonly
operation getAllCompanies {
    output: CompaniesResponse,
}

apply getAllCompanies @examples([
    {
        title: "Invoke getAllCompanies",
        output: {
            body: [
                {
                    id: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
                    settings: {"region": "region"},
                    stringAttribute1: "test 1",
                    stringAttribute2: "test 2",
                    longAttribute1: 1,
                    booleanAttribute: false,
                    created: "2022-03-07T00:00:00.001Z",
                    updated: "2022-03-07T00:00:00.001Z"
                }
            ]
        }
    }
])

@http(method: "POST", uri: "/v1/companies", code: 200)
operation createCompany {
    input: CompanyRequest,
    output: CompanyResponse,
}

apply createCompany @examples([
    {
        title: "Invoke createCompany",
        input: {
            body: {
                settings: {
                    test: "test"
                },
                stringAttribute1: "test",
                stringAttribute2: "test",
                longAttribute1: 1,
                booleanAttribute: true
            }
        },
        output: {
            id: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
            settings: { test: "test"},
            stringAttribute1: "test",
            stringAttribute2: "test",
            longAttribute1: 1,
            booleanAttribute: true,
            created: "2022-03-07T00:00:00.001Z",
            updated: "2022-03-07T00:00:00.001Z"            
        }
    }
])

@http(method: "PATCH", uri: "/v1/companies", code: 200)
operation updateCompany {
    input: CompanyRequest,
    output: CompanyResponse,
}

apply updateCompany @examples([
    {
        title: "Invoke updateCompany",
        input: {
            body: {
                id: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
                settings: {
                    test: "test"
                },
                stringAttribute1: "test",
                stringAttribute2: "test",
                longAttribute1: 1,
                booleanAttribute: true
            }
        },
        output: {
            id: "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
            settings: { test: "test"},
            stringAttribute1: "test",
            stringAttribute2: "test",
            longAttribute1: 1,
            booleanAttribute: true,
            created: "2022-03-07T00:00:00.001Z",
            updated: "2022-03-07T00:00:00.001Z"            
        }
    }
])

@http(method: "DELETE", uri: "/v1/companies/{companyId}", code: 204)
@idempotent
operation deleteCompany {
    input: CompanyIdRequest,
}

apply deleteCompany @examples([
    {
        title: "Invoke deleteCompany",
        input: {
            companyId: "7059f786-4633-4ace-a412-2f2e90556f08"
        },
    }
])

// --------- REQUESTS -------------

structure CompanyIdRequest {
    @httpLabel
    @required
    companyId: CompanyId
}

structure CompanyRequest {
    @httpPayload
    @required
    body: CompanyRequestBody
}

structure CompanyRequestBody {
    id: CompanyId,
    settings: Document,
    stringAttribute1: String,
    stringAttribute2: String,
    longAttribute1: Long,
    booleanAttribute: Boolean
}

// --------- RESPONSES -------------

structure CompanyResponse {
    @required
    id: CompanyId,
    settings: Document,
    stringAttribute1: String,
    stringAttribute2: String,
    longAttribute1: Long,
    booleanAttribute: Boolean,
    @required
    created: DateWithTime,
    @required
    updated: DateWithTime
}

structure CompaniesResponse {
    @httpPayload
    @required
    body: CompaniesResponseList
}

list CompaniesResponseList {
    member: CompanyResponse
}