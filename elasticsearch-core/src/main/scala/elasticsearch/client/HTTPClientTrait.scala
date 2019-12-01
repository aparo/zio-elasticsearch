/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import _root_.elasticsearch.requests.ActionRequest
import _root_.elasticsearch.{ ElasticSearch, ZioResponse }
import com.softwaremill.sttp._
import com.softwaremill.sttp.asynchttpclient.zio.AsyncHttpClientZioBackend
import elasticsearch.exception._
import io.circe.{ Decoder, Encoder }
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import zio.ZIO

import scala.concurrent.duration._
import scala.util.Random
import cats.implicits._
import izumi.logstage.api.IzLogger

case class ZioClient(
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
    extends ElasticSearch
    with ClientActionResolver {

  override def convertResponse[T: Encoder: Decoder](request: ActionRequest)(
    eitherResponse: Either[FrameworkException, ESResponse]
  ): Either[FrameworkException, T] =
    for {
      resp <- eitherResponse
      json <- resp.json.leftMap(e => FrameworkException(e))
      res <- json.as[T].leftMap(e => FrameworkException(e))
    } yield res

  override def concreteIndex(index: String): String = index

  override def concreteIndex(index: Option[String]): String = index.getOrElse("default")

  implicit val httpClient = {
    val cfg = new DefaultAsyncHttpClientConfig.Builder()
    useSSL match {
      case false =>
//        cfg.build()
      case true =>
        if (validateSSLCertificates) {
          cfg.setUseOpenSsl(true)
        } else {
          // we disable certificate check

          import io.netty.handler.ssl.{ SslContextBuilder, SslProvider }
          import io.netty.handler.ssl.util.InsecureTrustManagerFactory
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

  lazy val hosts: Seq[String] = servers.map(_.httpUrl(useSSL))

  def getHost: String = Random.shuffle(hosts).head

  def doCall(
    method: String,
    url: String
  ): ZioResponse[ESResponse] =
    doCall(method, url, None, Map.empty[String, String])

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
      case "GET"    => sttp.get(uri)
      case "POST"   => sttp.post(uri)
      case "PUT"    => sttp.put(uri)
      case "DELETE" => sttp.delete(uri)
      case "HEAD"   => sttp.head(uri)
      case "PATCH"  => sttp.patch(uri)
      //            case "CONNECT" => request.connect(uri)
      case "OPTIONS" => sttp.options(uri)
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
    val result = request
      .send()
      .map { response =>
        val resp = ESResponse(
          status = response.code,
          body = response.body match {
            case Left(value)  => value
            case Right(value) => value
          }
        )
        logger.debug(s"""response:$resp""")
        resp
      }
      .mapError(e => FrameworkException(s"Failed request: $request", e))

    result
  }

  def close(): ZioResponse[Unit] =
    for {
      blk <- this.bulker
      _ <- blk.close()
      _ <- ZIO.effectTotal(httpClient.close())
    } yield ()

  override def doCall(
    request: ActionRequest
  ): ZioResponse[ESResponse] =
    doCall(
      method = request.method,
      url = request.urlPath,
      body = bodyAsString(request.body),
      queryArgs = request.queryArgs
    )

}

object ZioClient {
  def apply(host: String, port: Int)(implicit logger: IzLogger): ZioClient = new ZioClient(
    List(ServerAddress(host, port))
  )
}
