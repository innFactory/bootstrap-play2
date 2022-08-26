$version: "1.0"

namespace de.innfactory.bootstrapplay2.apidefinition

use smithy4s.api#simpleRestJson

@simpleRestJson
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

@http(method: "GET", uri: "/v1/companies", code: 200)
@readonly
operation getAllCompanies {
    output: CompaniesResponse,
}

@http(method: "POST", uri: "/v1/companies", code: 200)
@readonly
operation createCompany {
    input: CompanyRequest,
    output: CompanyResponse,
}

@http(method: "PATCH", uri: "/v1/companies", code: 200)
@readonly
operation updateCompany {
    input: CompanyRequest,
    output: CompanyResponse,
}

@http(method: "DELETE", uri: "/v1/companies/{companyId}", code: 200)
@readonly
operation deleteCompany {
    input: CompanyIdRequest,
}

// --------- REQUESTS -------------

structure CompanyIdRequest {
    @httpLabel
    @required
    companyId: String
}

structure CompanyRequest {
    @httpPayload
    @required
    body: CompanyRequestBody
}

structure CompanyRequestBody {
    id: String,
    settings: Document,
    stringAttribute1: String,
    stringAttribute2: String,
    longAttribute1: Long,
    booleanAttribute: Boolean
}

// --------- RESPONSES -------------

structure CompanyResponse {
    @required
    id: String,
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