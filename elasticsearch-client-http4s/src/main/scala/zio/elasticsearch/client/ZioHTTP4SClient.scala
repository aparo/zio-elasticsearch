/*
 * Copyright 2018-2022 - Alberto Paro on Apache 2 Licence. All Rights Reserved.
 */

package zio.elasticsearch.client

import java.security.SecureRandom
import java.security.cert.X509Certificate
import _root_.zio.elasticsearch.{ ElasticSearchService, IngestService, _ }
import cats.effect._
import zio.elasticsearch.ElasticSearch
import zio.elasticsearch.client.RequestToCurl.toCurl
import zio.elasticsearch.orm.ORMService

import javax.net.ssl.{ SSLContext, X509TrustManager }
import org.http4s.client.Client
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.headers.{ Authorization, `Content-Type` }
import org.http4s.{ Request, _ }
import zio._

import zio.console.Console
import zio.exception._
import zio.interop.catz._

import zio.logging.{ LogLevel, Logging }
import zio.schema.SchemaService
import zio.elasticsearch.schema.ElasticSearchSchemaManagerService
import org.typelevel.ci._
import zio.Duration

import scala.concurrent.ExecutionContext

private[client] case class ZioHTTP4SClient(
  blockingEC: ExecutionContext,
  elasticSearchConfig: ElasticSearchConfig
)(implicit val runtime: Runtime[Any])
    extends elasticsearch.HTTPService {
  private lazy val _http4sClient: Resource[Task, Client[Task]] = {
    val sslContext: SSLContext =
      if (elasticSearchConfig.useSSL) {
        if (elasticSearchConfig.validateSSLCertificates) {
          SSLContext.getDefault
        } else {
          // we disable certificate check
          // You need to create a SSLContext with your own TrustManager and create HTTPS scheme using this context. Here is the code,

          val sslContext = SSLContext.getInstance("SSL");

          // set up a TrustManager that trusts everything
          sslContext.init(
            null,
            Array {
              new X509TrustManager() {
                override def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = {}

                override def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = {}

                override def getAcceptedIssuers: Array[X509Certificate] = null
              }
            },
            new SecureRandom()
          )
          sslContext
          //.setAcceptAnyCertificate(true)
          //          cfg.build()
        }
      } else SSLContext.getDefault

    BlazeClientBuilder[Task](blockingEC)
      .withSslContext(sslContext)
      .withIdleTimeout(elasticSearchConfig.timeout.asScala)
      .withMaxTotalConnections(elasticSearchConfig.concurrentConnections)
      .resource
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
    val headersObjects: List[Header.Raw] =
      (Header.Raw(CIString("Accept"), "application/json") :: headers.map {
        case (key, value) =>
          Header.Raw(CIString(key), value)
      }.toList) ++ (if (elasticSearchConfig.user.isDefined && elasticSearchConfig.user.get.nonEmpty) {
                      List(
                        Header.Raw(
                          ci"Authorization",
                          BasicCredentials(
                            elasticSearchConfig.user.get,
                            elasticSearchConfig.password.getOrElse("")
                          ).token
                        )
                      )
                    } else Nil)

    var request = Request[Task](
      resolveMethod(method),
      uri,
      headers = Headers(headersObjects),
      body = body.map(a => fs2.Stream(a).through(fs2.text.utf8Encode)).getOrElse(EmptyBody)
    )

    // we manage headers
    if (url.contains("_bulk")) {
      request = request.withContentType(ndjsonContent)
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

    ZIO.logDebug(s"${toCurl(request)}") *>
      _http4sClient
        .use(_.run(request).use { response =>
          for {
            body <- response.bodyText.compile.toList
            _ <- ZIO.logDebug(RequestToCurl.responseToString(response, body.mkString("")))
          } yield ESResponse(status = response.status.code, body = body.mkString(""))

        })
        .mapError(e => FrameworkException(e))
  }

}

object ZioHTTP4SClient {
  val live: ZLayer[ElasticSearchConfig, Nothing, HTTPService] =
    ZLayer.fromServices[Logger[String], Blocking.Service, ElasticSearchConfig, HTTPService] {
      (loggingService, blockingService, elasticSearchConfig) =>
        ZioHTTP4SClient(loggingService, blockingService.blockingExecutor.asEC, elasticSearchConfig)(Runtime.default)
    }

  val fromElasticSearch: ZLayer[ElasticSearch, Nothing, HTTPService] =
    ZLayer.fromServicesM[Logger[String], Blocking.Service, ElasticSearch, Any, Nothing, HTTPService] {
      (loggingService, blockingService, elasticSearch) =>
        for {
          esConfig <- elasticSearch.esConfig
        } yield ZioHTTP4SClient(loggingService, blockingService.blockingExecutor.asEC, esConfig)(Runtime.default)
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

  val fullLayer: ZLayer[Console with clock.Clock with ElasticSearchConfig, Nothing, ElasticSearchEnvironment] = {
    ZioHTTP4SClient.live >+> ElasticSearchService.live >+> IndicesService.live >+>
      ClusterService.live >+> IngestService.live >+> NodesService.live >+> SnapshotService.live >+>
      TasksService.live >+> ElasticSearchSchemaManagerService.liveInMemory >+> ORMService.live
  }

  def fullFromConfig(
    elasticSearchConfig: ElasticSearchConfig,
    loggingService: ZLayer[Console with clock.Clock, Nothing, Logging]
  ): ZLayer[Console with clock.Clock, Nothing, ElasticSearchEnvironment] = {
    val configService: Layer[Nothing, ElasticSearchConfig] =
      ZLayer.succeed[ElasticSearchConfig](elasticSearchConfig)
    val blockingService: Layer[Nothing, Blocking] = Blocking.live
    val httpService: ZLayer[Console with clock.Clock, Nothing, HTTPService] =
      (loggingService ++ blockingService ++ configService) >>> ZioHTTP4SClient.live
    val baseElasticSearchService: ZLayer[Console with clock.Clock, Nothing, Has[
      ElasticSearchService
    ]] = (loggingService ++ httpService ++ configService) >>> ElasticSearchService.live
    val indicesService: ZLayer[Console with clock.Clock, Nothing, IndicesService] =
      baseElasticSearchService >>> IndicesService.live
    val clusterService = indicesService >>> ClusterService.live
    val ingestService = baseElasticSearchService >>> IngestService.live
    val nodesService = baseElasticSearchService >>> NodesService.live
    val snapshotService = baseElasticSearchService >>> SnapshotService.live
    val tasksService = baseElasticSearchService >>> TasksService.live
    // with in memory SchemaService
    //ElasticSearchSchemaManagerService
    val schemaService = loggingService >>> SchemaService.inMemory
    val elasticSearchSchemaManagerService = (indicesService ++ schemaService) >>> ElasticSearchSchemaManagerService.live

    val ormService = (schemaService ++ clusterService) >>> ORMService.live

    baseElasticSearchService ++ indicesService ++ clusterService ++ ingestService ++ nodesService ++ snapshotService ++
      tasksService ++ schemaService ++ elasticSearchSchemaManagerService ++ ormService
  }

  def buildFromElasticsearch(
    logLayer: ZLayer[Console with clock.Clock, Nothing, Logging],
    esEmbedded: ZLayer[Any, Throwable, ElasticSearch]
  ): ZLayer[Console with clock.Clock, Throwable, ElasticSearchEnvironment] = {
    val blockingService: Layer[Nothing, Blocking] = Blocking.live
    val httpLayer = (logLayer ++ esEmbedded ++ blockingService) >>> ZioHTTP4SClient.fromElasticSearch
    val baseElasticSearchService = (logLayer ++ httpLayer ++ esEmbedded) >>> ElasticSearchService.fromElasticSearch
    val indicesService = baseElasticSearchService >>> IndicesService.live
    val clusterService = indicesService >>> ClusterService.live
    val ingestService = baseElasticSearchService >>> IngestService.live
    val nodesService = baseElasticSearchService >>> NodesService.live
    val snapshotService = baseElasticSearchService >>> SnapshotService.live
    val tasksService = baseElasticSearchService >>> TasksService.live
    // with in memory SchemaService
    //ElasticSearchSchemaManagerService
    val schemaService = logLayer >>> SchemaService.inMemory
    val elasticSearchSchemaManagerService = (indicesService ++ schemaService) >>> ElasticSearchSchemaManagerService.live
    val ormService = (schemaService ++ clusterService) >>> ORMService.live

    baseElasticSearchService ++ indicesService ++ clusterService ++ ingestService ++ nodesService ++ snapshotService ++
      tasksService ++ schemaService ++ elasticSearchSchemaManagerService ++ ormService
  }

}
