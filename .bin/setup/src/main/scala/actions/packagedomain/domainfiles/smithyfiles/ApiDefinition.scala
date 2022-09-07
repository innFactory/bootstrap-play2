package actions.packagedomain.domainfiles.smithyfiles

import actions.packagedomain.domainfiles.scalafiles.DomainModelId

case class ApiDefinition(packageDomain: String, packageName: String) extends SmithyDomainFile {
  val path = s"${System.getProperty("user.dir")}/modules/api"
  def name: String = s"${packageDomain}APIController"

  protected def getContent(): String = {
    val domainModelId = DomainModelId(packageDomain, packageName)

    s"""
      |$$version: "1.0"
      |
      |namespace de.innfactory.bootstrapplay2.api
      |use smithy4s.api#simpleRestJson
      |
      |@simpleRestJson
      |service $name {
      |    version: "1.0.0",
      |    operations: []
      |}
      |
      |// --------- OPERATIONS -------------
      |
      |
      |@http(method: "GET", uri: "/v1/$packageName", code: 200)
      |@readonly
      |operation getAll${packageName.capitalize} {
      |    output: $responsesName,
      |}
      |
      |@http(method: "GET", uri: "/v1/$packageName/{${domainModelId.nameLowerCased()}}", code: 200)
      |@readonly
      |operation get${packageDomain}ById {
      |    input: ${packageDomain}IdRequest,
      |    output: $responseName,
      |}
      |
      |@http(method: "POST", uri: "/v1/$packageName", code: 200)
      |@readonly
      |operation create$packageDomain {
      |    input: ${packageDomain}Request,
      |    output: $responseName,
      |}
      |
      |@http(method: "PATCH", uri: "/v1/$packageName", code: 200)
      |@readonly
      |operation update${packageDomain} {
      |    input: ${packageDomain}Request,
      |    output: $responseName,
      |}
      |
      |@http(method: "DELETE", uri: "/v1/$packageName/{${domainModelId.nameLowerCased()}}", code: 204)
      |@readonly
      |operation delete$packageDomain {
      |    input: ${packageDomain}IdRequest,
      |}
      |
      |// --------- REQUESTS -------------
      |
      |structure ${packageDomain}IdRequest {
      |    @httpLabel
      |    @required
      |    ${domainModelId.nameLowerCased()}: String
      |}
      |
      |structure ${packageDomain}Request {
      |    @httpPayload
      |    @required
      |    body: $requestBodyName
      |}
      |
      |structure $requestBodyName {
      |    id: String
      |}
      |
      |// --------- RESPONSES -------------
      |
      |structure $responseName {
      |    @required
      |    id: String,
      |    @required
      |    created: DateWithTime,
      |    @required
      |    updated: DateWithTime
      |}
      |
      |structure $responsesName {
      |    @httpPayload
      |    @required
      |    body: ${packageName.capitalize}ResponseList
      |}
      |
      |list ${packageName.capitalize}ResponseList {
      |    member: $responseName
      |}
      |""".stripMargin
  }
}
