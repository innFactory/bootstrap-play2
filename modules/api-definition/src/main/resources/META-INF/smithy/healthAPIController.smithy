$version: "2.0"

namespace de.innfactory.bootstrapplay2.api

use smithy4s.api#simpleRestJson

@simpleRestJson
service HealthAPIController {
    version: "1.0.0",
    operations: [ping, liveness, readiness]
}

@http(method: "GET", uri: "/", code: 200)
@readonly
operation ping {}

@http(method: "GET", uri: "/liveness", code: 200)
@readonly
operation liveness {}

@http(method: "GET", uri: "/readiness", code: 200)
@readonly
operation readiness {}