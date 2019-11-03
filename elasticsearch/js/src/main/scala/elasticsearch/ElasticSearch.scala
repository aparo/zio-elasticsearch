package elasticsearch

import elasticsearch.exception.FrameworkException
import cats.data.EitherT
import elasticsearch.client.{ ClientActionResolver, ESResponse, ServerAddress }
import elasticsearch.requests.{ ActionRequest, DeleteRequest, IndexRequest, UpdateRequest }
import elasticsearch.responses.{ DeleteResponse, IndexResponse, UpdateResponse }
import elasticsearch.responses.indices.{
  IndicesExistsResponse,
  IndicesFlushResponse,
  IndicesOpenResponse,
  RefreshResponse
}

import scala.concurrent.{ ExecutionContext, Future }
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.scalajs.dom.ext.Ajax
import cats.implicits._
import elasticsearch.ZioResponse
import elasticsearch.nosql.AbstractClient

final case class ElasticSearch(
  baseUrl: String,
  akkaSystem: ActorSystem,
  servers: List[ServerAddress] = Nil
) extends AbstractClient
    with ExtendedClientManagerTrait
    with ClientActionResolver {

  import _root_.elasticsearch.managers._

  var applicationName: String = "scalajs"

  lazy val mappings =
    new elasticsearch.mappings.MappingManager(this, akkaSystem)

  //  lazy val cat = new CatManager(this)
  lazy val snapshot = new SnapshotManager(this)
  //  lazy val language = new LanguageManager(this)
  lazy val indices = new IndicesManager(this)
  lazy val cluster = new ClusterManager(this)
  lazy val nodes = new NodesManager(this)
  //  lazy val benchmark = new  BenchmarkManager(this)
  //lazy val graph = new GraphManager(this)
  lazy val tasks = new TasksManager(this)
  lazy val ingest = new IngestManager(this)

  val creationSleep = 1000

  implicit def executionContext: ExecutionContext = queue

  def concreteIndex(index: String): String = index

  def concreteIndex(index: Option[String] = None): String =
    index.getOrElse(ElasticSearchConstants.defaultDB)

  override def exists(
    indices: String*
  ): ZioResponse[IndicesExistsResponse] =
    this.indices.exists(indices)

  override def flush(
    indices: String*
  ): ZioResponse[IndicesFlushResponse] =
    this.indices.flush(indices)

  override def refresh(
    indices: String*
  ): ZioResponse[RefreshResponse] =
    this.indices.refresh(indices)

  override def open(
    index: String
  ): ZioResponse[IndicesOpenResponse] =
    this.indices.open(index)

  def flushBulk(
    async: Boolean
  ): ZioResponse[IndicesFlushResponse] =
    this.indices.flush()

  override def addToBulk(
    action: IndexRequest
  ): ZioResponse[IndexResponse] = {
    implicit val context = elasticsearch.nosql.NoSqlContext.SystemUser
    // in JS we don't manage bulk
    this.indexDocument(action)

  }

  override def addToBulk(
    action: DeleteRequest
  ): ZioResponse[DeleteResponse] = {
    implicit val context = elasticsearch.nosql.NoSqlContext.SystemUser
    // in JS we don't manage bulk
    this.delete(action)

  }

  override def addToBulk(
    action: UpdateRequest
  ): ZioResponse[UpdateResponse] = {
    implicit val context = elasticsearch.nosql.NoSqlContext.SystemUser
    // in JS we don't manage bulk
    this.update(action)
  }

  override def doCall(
    request: ActionRequest
  ): ZioResponse[ESResponse] =
    doCall(
      method = request.method,
      url = request.urlPath,
      body = bodyAsString(request.body),
      queryArgs = request.queryArgs
    )

  def doCall(
    method: String,
    url: String,
    body: Option[String],
    queryArgs: Map[String, String]
  ): ZioResponse[ESResponse] = {

    var newPath: String =
      if (url.startsWith("/")) this.baseUrl + url else this.baseUrl + "/" + url
    if (queryArgs.nonEmpty) {
      newPath = addGetParam(newPath, queryArgs)
    }

    val data: String = body.orNull

    method match {
      case "get" =>
        EitherT.right[FrameworkException](
          Ajax.get(newPath, data = data, headers = headers).map { xhr =>
            ESResponse(xhr.status, xhr.responseText)
          }
        )

      case "post" =>
        EitherT.right[FrameworkException](
          Ajax.post(newPath, data = data, headers = headers).map { xhr =>
            ESResponse(xhr.status, xhr.responseText)
          }
        )
      case "put" =>
        EitherT.right[FrameworkException](
          Ajax.put(newPath, data = data, headers = headers).map { xhr =>
            ESResponse(xhr.status, xhr.responseText)
          }
        )
      case "delete" =>
        EitherT.right[FrameworkException](
          Ajax.delete(newPath, data = data, headers = headers).map { xhr =>
            ESResponse(xhr.status, xhr.responseText)
          }
        )
    }
  }
}
