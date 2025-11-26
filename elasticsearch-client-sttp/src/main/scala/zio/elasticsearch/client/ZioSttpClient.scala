/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.elasticsearch.client

import java.net.http.HttpClient
import java.net.http.HttpClient.{ Redirect, Version }
import java.net.{ CookieManager, CookiePolicy }
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.{ SSLContext, SSLParameters, TrustManager, X509TrustManager }

import _root_.zio.elasticsearch._
import sttp.client4._
import sttp.client4.httpclient.zio.{ HttpClientZioBackend, SttpClient }
import sttp.client4.prometheus.PrometheusBackend

import zio._
import zio.elasticsearch.ElasticSearch
import zio.elasticsearch.common.Method
import zio.exception._
case class ZioSttpClient(
  elasticSearchConfig: ElasticSearchConfig,
  sttpClient: SttpClient
) extends ElasticSearchHttpService {

  override def doCall(
    method: Method,
    url: String,
    body: Option[String],
    queryArgs: Map[String, String],
    headers: Map[String, String]
  ): ZIO[Any, FrameworkException, ESResponse] = {
    val path: String = if (url.startsWith("/")) url else "/" + url
    val newPath = elasticSearchConfig.getHost + path.replaceAll("//", "/")

    val uri = uri"$newPath?$queryArgs"

    var request = method match {
      case Method.GET                    => basicRequest.get(uri)
      case Method.POST                   => basicRequest.post(uri)
      case Method.PUT                    => basicRequest.put(uri)
      case Method.DELETE                 => basicRequest.delete(uri)
      case Method.HEAD                   => basicRequest.head(uri)
      case Method.PATCH                  => basicRequest.patch(uri)
      case Method.OPTIONS                => basicRequest.options(uri)
      case Method.CONNECT | Method.TRACE => basicRequest.post(uri)
      case Method.CUSTOM(_)              => basicRequest.post(uri)
    }

    if (headers.nonEmpty)
      request = request.headers(headers)

    // we manage headers
    if (url.contains("_bulk")) {
      request = request.contentType("application/x-ndjson", "UTF-8")
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
          case _ if method == Method.DELETE =>
          //            ElasticSearchKamon.delete.withoutTags().increment()
          case _ =>
        }
      }
    }

    if (elasticSearchConfig.user.isDefined && elasticSearchConfig.user.get.nonEmpty) {
      request = request.auth.basic(elasticSearchConfig.user.get, elasticSearchConfig.password.getOrElse(""))
    }

    if (body.nonEmpty && method != Method.HEAD)
      request = request.body(body.getOrElse(""))

    val result = for {
      _ <- ZIO.logDebug(s"${request.toCurl}")
      response <- sttpClient.send(request).mapError(e => FrameworkException(e))
    } yield ESResponse(
      status = response.code.code,
      body = response.body match {
        case Left(value)  => value
        case Right(value) => value
      }
    )
    result.mapError(e => FrameworkException(e))
  }

  def close(): ZIO[Any, Nothing, Unit] =
    sttpClient.close().mapError(e => FrameworkException(e)).ignore

}

object ZioSttpClient {

  def buildClientFromConfig(elasticSearchConfig: ElasticSearchConfig): HttpClient = {
    val cfg = HttpClient.newBuilder()
    // we setup SSL
    if (elasticSearchConfig.useSSL) {
      if (!elasticSearchConfig.validateSSLCertificates) {
        //        cfg.sslParameters(true)
        //      } else {
        // we disable certificate check
        val trustAllCerts = Array[TrustManager](new X509TrustManager() {
          def getAcceptedIssuers: Array[X509Certificate] = new Array[X509Certificate](0)

          def checkClientTrusted(certs: Array[X509Certificate], authType: String): Unit = {}

          def checkServerTrusted(certs: Array[X509Certificate], authType: String): Unit = {}
        })

        // context.init(null, trustAllCerts, new java.security.SecureRandom());
        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, new SecureRandom)

        val parameters = new SSLParameters
        parameters.setEndpointIdentificationAlgorithm("HTTPS")

        cfg.sslContext(sc).sslContext(sc).sslParameters(parameters)
      }
    }

    // we setup auth

    // we setup cookies
    val manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL)
    cfg
      .version(Version.HTTP_2)
      .connectTimeout(elasticSearchConfig.timeout)
      .followRedirects(Redirect.ALWAYS)
      .cookieHandler(manager)

    cfg.build()
  }

  val liveBackend: ZLayer[ElasticSearchConfig, Throwable, SttpClient] =
    ZLayer.scoped(
      ZIO.acquireRelease(
        for {
          elasticSearchConfig <- ZIO.service[ElasticSearchConfig]
          client <- ZIO.attempt(buildClientFromConfig(elasticSearchConfig))
          bck <- ZIO.attempt(
            HttpClientZioBackend.usingClient(
              client
            )
          )
          withProm = if (elasticSearchConfig.prometheus) {
            PrometheusBackend(bck)
          } else bck
        } yield withProm
      )(_.close().ignore)
    )

  val live: ZLayer[SttpClient with ElasticSearchConfig, Nothing, ElasticSearchHttpService] =
    ZLayer {
      for {
        sttpClient <- ZIO.service[SttpClient]
        elasticSearchConfig <- ZIO.service[ElasticSearchConfig]
      } yield ZioSttpClient(sttpClient = sttpClient, elasticSearchConfig = elasticSearchConfig)
    }

  val configFromElasticSearch: ZLayer[ElasticSearch, Nothing, ElasticSearchConfig] = {
    ZLayer {
      for {
        elasticSearch <- ZIO.service[ElasticSearch]
        esConfig <- elasticSearch.esConfig
      } yield esConfig
    }
  }

  type ElasticSearchEnvironment = ElasticSearchService with ElasticSearchHttpService

  val fullLayer: ZLayer[ElasticSearchConfig, Throwable, ElasticSearchEnvironment] = {
    ZioSttpClient.liveBackend >+> ZioSttpClient.live >+> ElasticSearchService.live
  }

  def fullFromConfig(
    elasticSearchConfig: ElasticSearchConfig
  ): ZLayer[Any, Throwable, ElasticSearchEnvironment] = ZLayer.make[ElasticSearchEnvironment](
    ZLayer.succeed[ElasticSearchConfig](elasticSearchConfig),
    ZioSttpClient.liveBackend,
    ZioSttpClient.live,
    ElasticSearchService.live
  )

  def buildFromElasticsearch(
    esEmbedded: ZLayer[Any, Throwable, ElasticSearch]
  ): ZLayer[Any, Throwable, ElasticSearchEnvironment] =
    ZLayer.make[ElasticSearchEnvironment](
      esEmbedded,
      ZioSttpClient.configFromElasticSearch,
      ZioSttpClient.liveBackend,
      ZioSttpClient.live,
      ElasticSearchService.live
    )
}
