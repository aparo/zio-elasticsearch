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

import _root_.elasticsearch.{ ClusterSupport, ZioResponse }
import elasticsearch.exception._
import izumi.logstage.api.IzLogger
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import zio._
import sttp.client._
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.asynchttpclient.zio.AsyncHttpClientZioBackend
import scala.concurrent.duration._
import sttp.client.impl.zio._

case class ZioSttpClient(
  servers: List[ServerAddress],
  queueSize: Int = 10,
  user: Option[String] = None,
  password: Option[String] = None,
  bulkSize: Int = 100,
  timeout: Option[FiniteDuration] = None,
  applicationName: String = "es",
  useSSL: Boolean = false,
  validateSSLCertificates: Boolean = true
)(implicit val logger: IzLogger)
    extends HTTPClientTrait
    with ClusterSupport {

  implicit val httpClient = {
    val cfg = new DefaultAsyncHttpClientConfig.Builder()
    if (useSSL) {
      if (validateSSLCertificates) {
        cfg.setUseOpenSsl(true)
      } else {
        // we disable certificate check

        import io.netty.handler.ssl.util.InsecureTrustManagerFactory
        import io.netty.handler.ssl.{ SslContextBuilder, SslProvider }
        val sslContext = SslContextBuilder.forClient
          .sslProvider(SslProvider.JDK)
          .trustManager(InsecureTrustManagerFactory.INSTANCE)
          .build
        cfg.setSslContext(sslContext)
        //.setAcceptAnyCertificate(true)
        //          cfg.build()
      }
    }
    AsyncHttpClientZioBackend.usingConfig(cfg.build())
  }

  def doCall(
    method: String,
    url: String,
    body: Option[String],
    queryArgs: Map[String, String]
  ): ZioResponse[ESResponse] = {
    val path: String = if (url.startsWith("/")) url else "/" + url
    val newPath = getHost + path.replaceAll("//", "/")

    val uri = uri"$newPath?$queryArgs"

    var request = method.toUpperCase() match {
      case "GET"    => basicRequest.get(uri)
      case "POST"   => basicRequest.post(uri)
      case "PUT"    => basicRequest.put(uri)
      case "DELETE" => basicRequest.delete(uri)
      case "HEAD"   => basicRequest.head(uri)
      case "PATCH"  => basicRequest.patch(uri)
      //            case "CONNECT" => request.connect(uri)
      case "OPTIONS" => basicRequest.options(uri)
      //            case "TRACE"   => request.trace(uri)
    }

    // we manage headers
    if (url.contains("_bulk")) {
      //      headers ::= RawHeader("Content-Type", "application/x-ndjson")

      //      ElasticSearchKamon.bulk.withoutTags().increment()
    } else {
      request = request.contentType("application/json", "UTF-8")

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
      request = request.auth.basic(user.get, password.getOrElse(""))
    }

    if (body.nonEmpty && method != "head")
      request = request.body(body.getOrElse(""))

    val curl = request.toCurl

    logger.debug(s"$curl")
    val result = for {
      implicit0(client: SttpBackend[zio.Task, Nothing, WebSocketHandler]) <- httpClient
      response <- request.send().mapError(e => FrameworkException(e))
    } yield ESResponse(
      status = response.code.code,
      body = response.body match {
        case Left(value)  => value
        case Right(value) => value
      }
    )
//    request
//      .send()
//      .map { response =>
//        val resp =
//        logger.debug(s"""response:$resp""")
//        resp
//      }
//      .mapError(e => FrameworkException(s"Failed request: $request", e))
//
//    result
    result.mapError(e => FrameworkException(e))
  }

  override def close(): ZioResponse[Unit] =
    for {
      _ <- super.close()
      cl <- httpClient.mapError(e => FrameworkException(e))
      _ <- cl.close().mapError(e => FrameworkException(e))
    } yield ()

}

object ZioSttpClient {
  def apply(host: String, port: Int)(implicit logger: IzLogger): ZioSttpClient = new ZioSttpClient(
    List(ServerAddress(host, port))
  )
}
