/*
 * Copyright 2019 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package elasticsearch.client

import java.security.SecureRandom
import java.security.cert.X509Certificate

import _root_.elasticsearch.{ Service, ZioResponse }
import cats.effect._
import zio.exception._
import izumi.logstage.api.IzLogger
import javax.net.ssl.{ SSLContext, X509TrustManager }
import org.http4s.{ Request, _ }
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.headers.{ Authorization, `Content-Type` }
import zio._
import zio.interop.catz._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import RequestToCurl.toCurl
import elasticsearch.orm.ORMService
import elasticsearch.schema.ElasticSearchSchemaManagerService
import zio.schema.InMemorySchemaService

case class ZioHTTP4SClient(
  servers: List[ServerAddress],
  queueSize: Int = 10,
  user: Option[String] = None,
  password: Option[String] = None,
  bulkSize: Int = 100,
  timeout: Option[FiniteDuration] = None,
  applicationName: String = "es",
  useSSL: Boolean = false,
  validateSSLCertificates: Boolean = true
)(implicit val logger: IzLogger, blockingEC: ExecutionContext, runtime: Runtime[Any])
    extends HTTPClientTrait
    with Service
    with ElasticSearchSchemaManagerService.Live
    with InMemorySchemaService
    with ORMService.Live {

  private lazy val _http4sClient: Resource[Task, Client[Task]] = {
    val sslContext: SSLContext = if (useSSL) {
      if (validateSSLCertificates) {
        SSLContext.getDefault
      } else {
        // we disable certificate check

        // You need to create a SSLContext with your own TrustManager and create HTTPS scheme using this context. Here is the code,

        val sslContext = SSLContext.getInstance("SSL");

        // set up a TrustManager that trusts everything
        sslContext.init(
          null,
          Array({
            new X509TrustManager() {
              override def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = {}

              override def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = {}

              override def getAcceptedIssuers: Array[X509Certificate] = null
            }
          }),
          new SecureRandom()
        )
        sslContext
        //.setAcceptAnyCertificate(true)
        //          cfg.build()
      }
    } else SSLContext.getDefault

    BlazeClientBuilder[Task](blockingEC, sslContext = Some(sslContext)).resource
  }

  private def resolveMethod(method: String): Method =
    method.toUpperCase() match {
      case "GET"    => Method.GET
      case "POST"   => Method.POST
      case "PUT"    => Method.PUT
      case "DELETE" => Method.DELETE
      case "HEAD"   => Method.HEAD
      case "PATCH"  => Method.PATCH
      //            case "CONNECT" => request.connect(uri)
      case "OPTIONS" => Method.OPTIONS
      //            case "TRACE"   => request.trace(uri)
    }

  private val jsonContent = `Content-Type`(MediaType.application.json, Charset.`UTF-8`)
  private val ndjsonContent = `Content-Type`(new MediaType("application", "x-ndjson"), Charset.`UTF-8`)

  def doCall(
    method: String,
    url: String,
    body: Option[String],
    queryArgs: Map[String, String]
  ): ZioResponse[ESResponse] = {
    val path: String = if (url.startsWith("/")) url else "/" + url
    val newPath = getHost + path.replaceAll("//", "/")
    var uri = Uri.unsafeFromString(s"$newPath")

    queryArgs.foreach(v => uri = uri.withQueryParam(v._1, v._2))

    var request = Request[Task](
      resolveMethod(method),
      uri,
      headers = Headers.of(Header("Accept", "application/json")),
      body = body.map(a => fs2.Stream(a).through(fs2.text.utf8Encode)).getOrElse(EmptyBody)
    )

    // we manage headers
    if (url.contains("_bulk")) {
      request = request.withContentType(ndjsonContent)
      //      headers ::= RawHeader("Content-Type", "application/x-ndjson")

      //      ElasticSearchKamon.bulk.withoutTags().increment()
    } else {
      request = request.withContentType(jsonContent)

      val specials = url.split("/").filter(_.startsWith("_"))
      if (specials.nonEmpty) {
        specials.last match {
          case "_search" | "_scan" | "_scroll" =>
          //            ElasticSearchKamon.search.withoutTags().increment()
          case "_update" =>
          //            ElasticSearchKamon.update.withoutTags().increment()
          case "_settings" | "_mappings" | "_status" | "_state" | "_node" | "_nodes" =>
          //            ElasticSearchKamon.admin.withoutTags().increment()
          case _ if method == "delete" =>
          //            ElasticSearchKamon.delete.withoutTags().increment()
          case _ =>
        }
      }
    }

    if (user.isDefined && user.get.nonEmpty) {
      request = request.withHeaders(Authorization(BasicCredentials(user.get, password.getOrElse(""))))
    }

    logger.debug(s"${toCurl(request)}")
    _http4sClient
      .use(_.fetch(request) { response =>
        response.as[String].map(body => ESResponse(status = response.status.code, body = body))
      })
      .mapError(e => FrameworkException(e))
  }

  override def close(): ZioResponse[Unit] =
    super.close()

}

object ZioHTTP4SClient {
  def apply(
    host: String,
    port: Int
  )(implicit logger: IzLogger, blockingEC: ExecutionContext, runtime: Runtime[Any]): ZioHTTP4SClient =
    new ZioHTTP4SClient(
      List(ServerAddress(host, port))
    )
}
