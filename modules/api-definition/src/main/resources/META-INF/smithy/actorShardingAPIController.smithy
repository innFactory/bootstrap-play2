$version: "1.0"

namespace de.innfactory.bootstrapplay2.api
use smithy4s.api#simpleRestJson

@simpleRestJson
service ActorShardingAPIController {
    version: "1.0.0",
    operations: [
        helloworldViaSharding
    ]
}

// --------- OPERATIONS -------------

@http(method: "GET", uri: "/v1/public/helloworld/sharding/{query}", code: 200)
@readonly
operation helloworldViaSharding {
    input: HelloworldViaShardingRequest,
    output: HelloworldViaShardingResponse
}

// --------- REQUESTS -------------

structure HelloworldViaShardingRequest {
    @httpLabel
    @required
    query: String
}

// --------- RESPONSES -------------

structure HelloworldViaShardingResponse {
    @httpLabel
    @required
    answer: String
}