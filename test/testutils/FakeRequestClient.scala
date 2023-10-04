package testutils

import akka.stream.Materializer
import de.innfactory.smithy4play.client.{RequestClient, SmithyClientResponse}
import play.api.Application
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.{ExecutionContext, Future}

class FakeRequestClient(implicit application: Application, ec: ExecutionContext, mat: Materializer)
    extends RequestClient {
  override def send(
      method: String,
      path: String,
      headers: Map[String, Seq[String]],
      body: Option[Array[Byte]]
  ): Future[SmithyClientResponse] = {
    val baseRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(method, path)
      .withHeaders(headers.toList.flatMap(headers => headers._2.map(v => (headers._1, v))): _*)
    val res =
      if (body.isDefined) route(application, baseRequest.withBody(body.get)).get
      else
        route(
          application,
          baseRequest
        ).get

    for {
      result <- res
      headers = result.header.headers.map(v => (v._1, Seq(v._2)))
      body <- result.body.consumeData.map(_.toArrayUnsafe())
      bodyConsumed = if (result.body.isKnownEmpty) None else Some(body)
      contentType = result.body.contentType
      headersWithContentType =
        if (contentType.isDefined) headers + ("Content-Type" -> Seq(contentType.get)) else headers
    } yield SmithyClientResponse(bodyConsumed, headersWithContentType, result.header.status)
  }
}
