package actions.packagedomain.domainfiles.smithyfiles

import actions.packagedomain.domainfiles.scalafiles.DomainModelId
import config.SetupConfig

case class ApiDefinition(packageDomain: String, packageName: String) extends SmithyDomainFile {
  override def subPath = ""
  def name: String = s"${packageDomain}APIController"

  protected def getContent()(implicit config: SetupConfig): String = {
    val domainModelId = DomainModelId(packageDomain, packageName)

    val allOperationName = s"getAll${packageName.capitalize}"
    val getByIdOperationName = s"get${packageDomain}ById"
    val createOperationName = s"create$packageDomain"
    val updateOperationName = s"update$packageDomain"
    val deleteOperationName = s"delete$packageDomain"

    s"""
      |$$version: "1.0"
      |
      |namespace ${config.project.packagesRoot}.api
      |use smithy4s.api#simpleRestJson
      |
      |@simpleRestJson
      |service $name {
      |    version: "1.0.0",
      |    operations: [${Set(
        allOperationName,
        getByIdOperationName,
        createOperationName,
        updateOperationName,
        deleteOperationName
      ).mkString(", ")}]
      |}
      |
      |// --------- OPERATIONS -------------
      |
      |
      |@http(method: "GET", uri: "/v1/$packageName", code: 200)
      |@readonly
      |operation $allOperationName {
      |    output: $responsesName,
      |}
      |
      |@http(method: "GET", uri: "/v1/$packageName/{${domainModelId.nameLowerCased()}}", code: 200)
      |@readonly
      |operation $getByIdOperationName {
      |    input: ${packageDomain}IdRequest,
      |    output: $responseName,
      |}
      |
      |@http(method: "POST", uri: "/v1/$packageName", code: 200)
      |@readonly
      |operation $createOperationName {
      |    input: ${packageDomain}Request,
      |    output: $responseName,
      |}
      |
      |@http(method: "PATCH", uri: "/v1/$packageName", code: 200)
      |@readonly
      |operation $updateOperationName {
      |    input: ${packageDomain}Request,
      |    output: $responseName,
      |}
      |
      |@http(method: "DELETE", uri: "/v1/$packageName/{${domainModelId.nameLowerCased()}}", code: 204)
      |@readonly
      |operation $deleteOperationName {
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
