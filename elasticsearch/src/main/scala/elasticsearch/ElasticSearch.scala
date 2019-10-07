/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import elasticsearch.client._
import elasticsearch.orm.QueryBuilder
import elasticsearch.requests.{ DeleteRequest, IndexRequest, UpdateRequest }
import elasticsearch.responses._
import elasticsearch.responses.indices.{ FlushResponse, IndicesExistsResponse, OpenIndexResponse, RefreshResponse }
import io.circe._
import zio.{ Ref, ZIO }

import scala.concurrent._
import scala.concurrent.duration._

// scalastyle:off

trait ElasticSearch extends ExtendedClientManagerTrait with ClientActions with IndexResolverTrait {

  def bulkSize: Int
  def applicationName: String

  lazy val mappings =
    new elasticsearch.mappings.MappingManager(this)

  //activate debug
  /* Managers */

  import _root_.elasticsearch.managers._

  lazy val snapshot = new SnapshotManager(this)
  lazy val indices = new IndicesManager(this)
  lazy val cluster = new ClusterManager(this)
  lazy val nodes = new NodesManager(this)
  lazy val tasks = new TasksManager(this)
  lazy val ingest = new IngestManager(this)

  val dirty = Ref.make(false)

  var defaultTimeout = 1000.seconds
  var creationSleep = 500L
  var connectionLimits = 10
  var maxRetries = 2
  var ignoreLinkInBulk = false
  var defaultSettings = Settings.ElasticSearchBase
  var defaultTestSettings = Settings.ElasticSearchTestBase
  //default settings to build index
  //  var defaultIndex = "default"
  var alias = Set.empty[String]
  protected var innerBulkSize: Int = bulkSize
  var maxConcurrentBulk = 10
  var bulkMemMaxSize: Int = 1024 * 1024
  var bulkTimeout = 15.minutes
  //we manage dirty states
  private var linkedIndices = Set.empty[String] //indexes linked with this one

  def awaitResultBulk[T](t: Awaitable[T]) = Await.result(t, bulkTimeout)

  def hosts: Seq[String]

  def close(): ZioResponse[Unit]

  //  protected lazy val bulker = new StringBuilder
  //  protected var bulkerActions = 0

  def dropDatabase(index: String): ZioResponse[Unit] =
    for {
      exists <- this.indices.exists(Seq(index))
      _ <- if (exists.isExists)(this.indices
        .delete(Seq(index))
        .andThen(this.cluster.health(waitForStatus = Some(WaitForStatus.yellow))))
      else ZIO.unit
      dir <- dirty
      _ <- dir.set(false)
    } yield ()

//    val exists = awaitResult().value)
//    exists.map { ex =>
//      if (ex.isExists) {
//        val res = this.indices.delete(Seq(index))
//        awaitResult(res.value)
//        val res2 =
//
//        this.awaitResult(res2.value)
//      }
//
//    }

  def refresh(): ZioResponse[Unit] =
    for {
      _ <- this.flushBulk(false)
      _ <- indices.refresh(linkedIndices.toList)
      dir <- dirty
      _ <- dir.set(false)
    } yield ()

  /* Sequence management */
  /* Get a new value for the id */
  def getSequenceValue(
    id: String,
    index: String = ElasticSearchConstants.SEQUENCE_INDEX,
    docType: String = "sequence"
  )(
    implicit qContext: ESNoSqlContext
  ): ZioResponse[Option[Long]] =
    this
      .indexDocument(index, Some(id), body = JsonObject.empty)(
        qContext.systemNoSQLContext()
      )
      .map { x =>
        Option(x.version)
      }

  /* Reset the sequence for the id */
  def resetSequence(id: String)(implicit qContext: ESNoSqlContext): ZioResponse[Unit] =
    this
      .delete(ElasticSearchConstants.SEQUENCE_INDEX, id)(
        qContext.systemNoSQLContext()
      )
      .map { _ =>
        ()
      }

  def encodeBinary(data: Array[Byte]): String =
    new String(java.util.Base64.getMimeEncoder.encode(data))

  def decodeBinary(data: String): Array[Byte] =
    java.util.Base64.getMimeDecoder.decode(data)

  def copyData(
    queryBuilder: QueryBuilder,
    destIndex: String,
    destType: Option[String] = None,
    callbackSize: Int = 10000,
    callback: Int => Unit = { _ =>
      },
    transformSource: HitResponse => JsonObject = {
      _.source
    }
  ) = {
    val destT = destType.getOrElse(queryBuilder.docTypes.head)
    var size = 0
    queryBuilder.scanHits.foreach { hit =>
      size += 1
      addToBulk(
        IndexRequest(
          destIndex,
          Some(hit.id),
          body = transformSource(hit)
        )
      )
      if (size % callbackSize == 0) {
        callback(size)
      }
    }
    if (size > 0)
      callback(size)
    flush(destIndex)
  }

  //  /*
  //   * http://doc.QDB.com/qdb/guide/reference/master/docs-delete-by-query.html
  //   *
  //   * @param indices A list of indices to restrict the operation; use `_all` to perform the operation on all indices
  //   * @param docTypes A list of types to restrict the operation
  //   * @param body body the body of the call
  //   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
  //   * @param source The URL-encoded query definition (instead of using the request body)
  //   * @param analyzer The analyzer to use for the query string
  //   * @param defaultOperator The default operator for query string query (AND or OR)
  //   * @param consistency Specific write consistency setting for the operation
  //   * @param replication Specific replication type
  //   * @param q Query in the Lucene query string syntax
  //   * @param df The field to use as default where no field prefix is given in the query string
  //   * @param routing Specific routing value
  //   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
  //   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
  //   * @param timeout Explicit operation timeout
  //   */
  //  def deleteByQuery(
  //                     indices: Seq[String],
  //                     docTypes: Seq[String],
  //                     body: String = "{}"
  //                   )(implicit qContext: ESNoSqlContext): Unit = {
  //
  //    val qb = QueryBuilder(indices = indices,
  //      docTypes = docTypes,
  //      extraBody = Json.parse(body).asOpt[JsonObject])
  //    qb.scanHits.foreach { searchHit =>
  //      this.addToBulk(new DeleteRequest(searchHit.index, searchHit.docType, searchHit.id))
  //    }
  //    this.flush(indices: _*)
  //  }

  //  /** Helper function to eecute a count */
  //  def count(index: String, docType: String)(
  //    implicit qContext: ESNoSqlContext): Future[Long] = {
  //    val qb = QueryBuilder(indices = Seq(index), docTypes = Seq(docType), size = 0)
  //    qb.count
  //  }
  //
  //  def count(query: Query, indices: Seq[String] = Nil, docTypes: Seq[String] = Nil)(
  //    implicit qContext: ESNoSqlContext): Future[Long] = {
  //    val qb = QueryBuilder(indices = indices, docTypes = docTypes, queries = List(query), size = 0)
  //    qb.count
  //  }

  /* Connection qContext actions */

  def reindex(index: String, docType: String = "_doc")(
    implicit qContext: ESNoSqlContext
  ): Unit = {
    val qb = QueryBuilder(indices = List(index), docTypes = List(docType))(
      qContext.systemNoSQLContext()
    )
    qb.scanHits.foreach { searchHit =>
      this.addToBulk(
        IndexRequest(
          searchHit.index,
          Some(searchHit.id),
          body = searchHit.source
        )
      )
    }
    flush(index)

  }

  def getIds(index: String, docType: String)(
    implicit qContext: ESNoSqlContext
  ) =
    QueryBuilder(
      indices = List(index),
      docTypes = List(docType),
      bulkRead = 5000
    )(qContext.systemNoSQLContext()).valueList[String]("_id")

  def getIndicesAlias(): ZioResponse[Map[String, List[String]]] =
    this.cluster.state().map { response =>
      response.metadata.indices.map { i =>
        i._1 -> i._2.aliases
      }
    }

  override def exists(
    indices: String*
  ): ZioResponse[IndicesExistsResponse] =
    this.indices.exists(indices)

  override def flush(
    indices: String*
  ): ZioResponse[FlushResponse] =
    this.indices.flush(indices)

  override def refresh(
    indices: String*
  ): ZioResponse[RefreshResponse] =
    this.indices.refresh(indices)

  override def open(
    index: String
  ): ZioResponse[OpenIndexResponse] =
    this.indices.open(index)

  protected var bulkerStarted: Boolean = false

  protected lazy val bulker = Bulker.apply(this, bulkSize = this.innerBulkSize)

  override def addToBulk(
    action: IndexRequest
  ): ZioResponse[IndexResponse] =
    for {
      blkr <- bulker
      _ <- blkr.add(action)
    } yield
      IndexResponse(
        index = action.index,
        id = action.id.getOrElse(""),
        version = 1
      )

  override def addToBulk(
    action: DeleteRequest
  ): ZioResponse[DeleteResponse] =
    for {
      blkr <- bulker
      _ <- blkr.add(action)
    } yield DeleteResponse(action.index, action.id)

  override def addToBulk(
    action: UpdateRequest
  ): ZioResponse[UpdateResponse] =
    for {
      blkr <- bulker
      _ <- blkr.add(action)
    } yield UpdateResponse(action.index, action.id)

  def executeBulk(body: String, async: Boolean = false): ZioResponse[BulkResponse] =
    if (body.nonEmpty) {
      this.bulk(body)
    } else ZIO.succeed(BulkResponse(0, false, Nil))

  override def flushBulk(
    async: Boolean = false
  ): ZioResponse[FlushResponse] =
    for {
      blkr <- bulker
      _ <- blkr.flushBulk()
    } yield FlushResponse()

}
// scalastyle:on
