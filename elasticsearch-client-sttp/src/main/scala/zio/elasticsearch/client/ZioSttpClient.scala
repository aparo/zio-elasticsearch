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

package zio.elasticsearch.client

import _root_.zio.elasticsearch._
import zio.exception._
import zio.elasticsearch.ElasticSearch
import zio.elasticsearch.orm.ORMService
import zio.elasticsearch.schema.ElasticSearchSchemaManagerService
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import sttp.client3._
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.client3.prometheus.PrometheusBackend
import zio._
import zio.schema.elasticsearch.SchemaService

case class ZioSttpClient(
  elasticSearchConfig: ElasticSearchConfig
) extends elasticsearch.HTTPService {

  implicit val httpClientBackend = {
    val cfg = new DefaultAsyncHttpClientConfig.Builder()
    if (elasticSearchConfig.useSSL) {
      if (elasticSearchConfig.validateSSLCertificates) {
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
      }
    }
    var client = AsyncHttpClientZioBackend.usingConfig(cfg.build())
    if (elasticSearchConfig.prometheus) {
      client = client.map(p => PrometheusBackend(p))
    }

    client
  }

  override def doCall(
    method: String,
    url: String,
    body: Option[String],
    queryArgs: Map[String, String],
    headers: Map[String, String]
  ): ZioResponse[ESResponse] = {
    val path: String = if (url.startsWith("/")) url else "/" + url
    val newPath = elasticSearchConfig.getHost + path.replaceAll("//", "/")

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
          case _ if method == "delete" =>
          //            ElasticSearchKamon.delete.withoutTags().increment()
          case _ =>
        }
      }
    }

    if (elasticSearchConfig.user.isDefined && elasticSearchConfig.user.get.nonEmpty) {
      request = request.auth.basic(elasticSearchConfig.user.get, elasticSearchConfig.password.getOrElse(""))
    }

    if (body.nonEmpty && method != "head")
      request = request.body(body.getOrElse(""))

    val result = for {
      _ <- ZIO.logDebug(s"${request.toCurl}")

      client <- httpClientBackend
      response <- client.send(request).mapError(e => FrameworkException(e))
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
    (for {
      cl <- httpClientBackend.mapError(e => FrameworkException(e))
      _ <- cl.close().mapError(e => FrameworkException(e))
    } yield ()).ignore

}

object ZioSttpClient {
  val live: ZLayer[ElasticSearchConfig, Nothing, HTTPService] =
    ZLayer.scoped {
      for {
        elasticSearchConfig <- ZIO.service[ElasticSearchConfig]
        service <- ZIO.acquireRelease(ZIO.succeed(ZioSttpClient(elasticSearchConfig)))(_.close())
      } yield service

    }

  val fromElasticSearch: ZLayer[ElasticSearch, Nothing, HTTPService] =
    ZLayer {
      for {
        elasticSearch <- ZIO.service[ElasticSearch]
        esConfig <- elasticSearch.esConfig
      } yield ZioSttpClient(esConfig)
    }

  type ElasticSearchEnvironment = ElasticSearchService
    with IndicesService
    with ORMService
    with ClusterService
    with IngestService
    with NodesService
    with SnapshotService
    with TasksService
    //    with SchemaService
    with ElasticSearchSchemaManagerService
    with ORMService

  val fullLayer: ZLayer[ElasticSearchConfig, Nothing, ElasticSearchEnvironment] = {
    ZioSttpClient.live >+> ElasticSearchService.live >+> IndicesService.live >+>
      ClusterService.live >+> IngestService.live >+> NodesService.live >+> SnapshotService.live >+>
      TasksService.live >+> ElasticSearchSchemaManagerService.liveInMemory >+> ORMService.live
  }

  def fullFromConfig(
    elasticSearchConfig: ElasticSearchConfig
  ): ZLayer[Any, Nothing, ElasticSearchEnvironment] = ZLayer.make[ElasticSearchEnvironment](
    ZLayer.succeed[ElasticSearchConfig](elasticSearchConfig),
    ZioSttpClient.live,
    ElasticSearchService.live,
    IndicesService.live,
    ClusterService.live,
    IngestService.live,
    NodesService.live,
    SnapshotService.live,
    TasksService.live,
    SchemaService.inMemory,
    ElasticSearchSchemaManagerService.live,
    ORMService.live
  )

  def buildFromElasticsearch(
    esEmbedded: ZLayer[Any, Throwable, ElasticSearch]
  ): ZLayer[Any, Throwable, ElasticSearchEnvironment] =
    ZLayer.make[ElasticSearchEnvironment](
      esEmbedded,
      ZioSttpClient.fromElasticSearch,
      ElasticSearchService.fromElasticSearch,
      IndicesService.live,
      ClusterService.live,
      IngestService.live,
      NodesService.live,
      SnapshotService.live,
      TasksService.live,
      //   // with in memory SchemaService
      SchemaService.inMemory,
      ElasticSearchSchemaManagerService.live,
      ORMService.live
    )
}
