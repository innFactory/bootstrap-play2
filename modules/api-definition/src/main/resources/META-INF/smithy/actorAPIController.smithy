$version: "1.0"

namespace de.innfactory.bootstrapplay2.api
use smithy4s.api#simpleRestJson

@simpleRestJson
service ActorAPIController {
    version: "1.0.0",
    operations: [
        helloworld
    ]
}

// --------- OPERATIONS -------------

@http(method: "GET", uri: "/v1/public/helloworld/{query}", code: 200)
@readonly
operation helloworld {
    input: HelloworldRequest,
    output: HelloworldResponse
}

// --------- REQUESTS -------------

structure HelloworldRequest {
    @httpLabel
    @required
    query: String
}

// --------- RESPONSES -------------

structure HelloworldResponse {
    @httpLabel
    @required
    answer: String
}