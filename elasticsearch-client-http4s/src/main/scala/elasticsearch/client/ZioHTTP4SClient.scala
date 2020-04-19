/*
 * Copyright 2019-2020 Alberto Paro
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

import _root_.elasticsearch._
import cats.effect._
import elasticsearch.client.RequestToCurl.toCurl
import elasticsearch.orm.ORMService
import javax.net.ssl.{SSLContext, X509TrustManager}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.headers.{Authorization, `Content-Type`}
import org.http4s.{Request, _}
import zio._
import zio.blocking.Blocking
import zio.console.Console
import zio.exception._
import zio.interop.catz._
import zio.logging.Logging.Logging
import zio.logging.{LogLevel, Logging}
import zio.schema.SchemaService

import scala.concurrent.ExecutionContext

private[client] case class ZioHTTP4SClient(
    loggingService: Logging.Service,
    blockingEC: ExecutionContext,
    elasticSearchConfig: ElasticSearchConfig
)(implicit val runtime: Runtime[Any])
    extends elasticsearch.HTTPService.Service {
  private lazy val _http4sClient: Resource[Task, Client[Task]] = {
    val sslContext: SSLContext = if (elasticSearchConfig.useSSL) {
      if (elasticSearchConfig.validateSSLCertificates) {
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
              override def checkClientTrusted(chain: Array[X509Certificate],
                                              authType: String): Unit = {}

              override def checkServerTrusted(chain: Array[X509Certificate],
                                              authType: String): Unit = {}

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

    BlazeClientBuilder[Task](blockingEC, sslContext = Some(sslContext))
      .withIdleTimeout(elasticSearchConfig.timeout)
      .withMaxTotalConnections(elasticSearchConfig.concurrentConnections)
      .resource
  }

  private def resolveMethod(method: String): Method =
    method.toUpperCase() match {
      case "GET" => Method.GET
      case "POST" => Method.POST
      case "PUT" => Method.PUT
      case "DELETE" => Method.DELETE
      case "HEAD" => Method.HEAD
      case "PATCH" => Method.PATCH
      //            case "CONNECT" => request.connect(uri)
      case "OPTIONS" => Method.OPTIONS
      //            case "TRACE"   => request.trace(uri)
    }

  private val jsonContent =
    `Content-Type`(MediaType.application.json, Charset.`UTF-8`)
  private val ndjsonContent =
    `Content-Type`(new MediaType("application", "x-ndjson"), Charset.`UTF-8`)

  override def doCall(
      method: String,
      url: String,
      body: Option[String],
      queryArgs: Map[String, String],
      headers: Map[String, String]
  ): ZioResponse[ESResponse] = {
    val path: String = if (url.startsWith("/")) url else "/" + url
    val newPath = elasticSearchConfig.getHost + path.replaceAll("//", "/")
    var uri = Uri.unsafeFromString(s"$newPath")

    queryArgs.foreach(v => uri = uri.withQueryParam(v._1, v._2))
    val headersObjects
      : List[Header] = Header("Accept", "application/json") :: headers.map {
      case (key, value) => Header(key, value)
    }.toList

    var request = Request[Task](
      resolveMethod(method),
      uri,
      headers = Headers(headersObjects),
      body = body
        .map(a => fs2.Stream(a).through(fs2.text.utf8Encode))
        .getOrElse(EmptyBody)
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
          case "_settings" | "_mappings" | "_status" | "_state" | "_node" |
              "_nodes" =>
          //            ElasticSearchKamon.admin.withoutTags().increment()
          case _ if method == "delete" =>
          //            ElasticSearchKamon.delete.withoutTags().increment()
          case _ =>
        }
      }
    }

    if (elasticSearchConfig.user.isDefined && elasticSearchConfig.user.get.nonEmpty) {
      request = request.withHeaders(
        Authorization(
          BasicCredentials(elasticSearchConfig.user.get,
                           elasticSearchConfig.password.getOrElse("")))
      )
    }

    loggingService.logger.log(LogLevel.Debug)(s"${toCurl(request)}")
    _http4sClient
      .use(_.fetch(request) { response =>
        response
          .as[String]
          .map(body => ESResponse(status = response.status.code, body = body))
      })
      .mapError(e => FrameworkException(e))
  }

}

object ZioHTTP4SClient {
  val live: ZLayer[Logging with Blocking with Has[ElasticSearchConfig],
                   Nothing,
                   Has[HTTPService.Service]] =
    ZLayer.fromServices[Logging.Service,
                        Blocking.Service,
                        ElasticSearchConfig,
                        HTTPService.Service] {
      (loggingService, blockingService, elasticSearchConfig) =>
        ZioHTTP4SClient(loggingService,
                        blockingService.blockingExecutor.asEC,
                        elasticSearchConfig)(Runtime.default)
    }

  val fromElasticSearch
    : ZLayer[Logging with Blocking with ElasticSearch.ElasticSearch,
             Nothing,
             Has[HTTPService.Service]] =
    ZLayer.fromServicesM[Logging.Service,
                         Blocking.Service,
                         ElasticSearch.Service,
                         Any,
                         Nothing,
                         HTTPService.Service] {
      (loggingService, blockingService, elasticSearch) =>
        for {
          esConfig <- elasticSearch.esConfig
        } yield
          ZioHTTP4SClient(loggingService,
                          blockingService.blockingExecutor.asEC,
                          esConfig)(Runtime.default)
    }

  type fullENV =
    Has[ORMService.Service] with Has[IngestService.Service] with Has[
      NodesService.Service] with Has[SnapshotService.Service] with Has[
      TasksService.Service]

  def fullFromConfig(
      elasticSearchConfig: ElasticSearchConfig,
      loggingService: ZLayer[Console with clock.Clock, Nothing, Logging]
  ): ZLayer[
    Console with clock.Clock with Any,
    Nothing,
    Has[ORMService.Service] with Has[IngestService.Service] with Has[
      NodesService.Service
    ] with Has[SnapshotService.Service] with Has[TasksService.Service]] = {
    val configService =
      ZLayer.succeed[ElasticSearchConfig](elasticSearchConfig)
    val blockingService: Layer[Nothing, Blocking] = Blocking.live
    val httpService = (loggingService ++ blockingService ++ configService) >>> ZioHTTP4SClient.live
    val baseElasticSearchService = (loggingService ++ httpService ++ configService) >>> ElasticSearchService.live
    val indicesService = baseElasticSearchService >>> IndicesService.live
    val clusterService = indicesService >>> ClusterService.live
    val ingestService = baseElasticSearchService >>> IngestService.live
    val nodesService = baseElasticSearchService >>> NodesService.live
    val snapshotService = baseElasticSearchService >>> SnapshotService.live
    val tasksService = baseElasticSearchService >>> TasksService.live
    // with in memory SchmeaService
    //ElasticSearchSchemaManagerService
    val schemaService = loggingService >>> SchemaService.inMemory
    val ormService = (schemaService ++ clusterService) >>> ORMService.live

    ormService ++ ingestService ++ nodesService ++ snapshotService ++ tasksService

  }

}
