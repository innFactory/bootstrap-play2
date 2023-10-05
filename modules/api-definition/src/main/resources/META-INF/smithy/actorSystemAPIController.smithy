$version: "2.0"

namespace de.innfactory.bootstrapplay2.api
use alloy#simpleRestJson

@simpleRestJson
service ActorSystemAPIController {
    version: "1.0.0",
    operations: [
        helloworldViaSystem
    ]
}

// --------- OPERATIONS -------------

@http(method: "GET", uri: "/v1/public/helloworld/system/{query}", code: 200)
@readonly
operation helloworldViaSystem {
    input: HelloworldViaSystemRequest,
    output: HelloworldViaSystemResponse
}

// --------- REQUESTS -------------

structure HelloworldViaSystemRequest {
    @httpLabel
    @required
    query: String
}

// --------- RESPONSES -------------

structure HelloworldViaSystemResponse {
    @httpLabel
    @required
    answer: String
}