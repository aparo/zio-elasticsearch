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

package zio.elasticsearch

import zio.auth.AuthContext
import zio.exception.FrameworkException
import zio.elasticsearch.client._
import zio.elasticsearch.requests._
import zio.elasticsearch.responses._
import zio.json._
import zio.json.ast._
import zio._
import zio.elasticsearch.common.{ DefaultOperator, ExpandWildcards }
import zio.schema.elasticsearch.annotations.{ CustomId, CustomIndex }
import zio.stream.ZSink
trait ElasticSearchService
    extends ExtendedClientManagerTrait
    with ClientActions
    with IndexResolverTrait
    with ClientActionResolver {
  def httpService: HTTPService
  def config: ElasticSearchConfig

  def applicationName: String

  //activate debug
  /* Managers */

  lazy val dirty = Ref.make(false)

  override def concreteIndex(index: String): String = config.concreteIndexName(index)

  override def concreteIndex(index: Option[String]): String =
    config.concreteIndexName(index.getOrElse("default"))

  def close(): ZIO[Any, FrameworkException, Unit] =
    for {
      blk <- this.bulker
      _ <- blk.close()
    } yield ()

  /* Sequence management */
  /* Get a new value for the id */
  def getSequenceValue(
    id: String,
    index: String = ElasticSearchConstants.SEQUENCE_INDEX,
    docType: String = "sequence"
  )(
    implicit
    authContext: AuthContext
  ): ZIO[Any, FrameworkException, Option[Long]] =
    this
      .indexDocument(index, id = Some(id), body = Json.Obj())(
        authContext.systemNoSQLContext()
      )
      .map { x =>
        Option(x.version)
      }

  /* Reset the sequence for the id */
  def resetSequence(id: String)(implicit authContext: AuthContext): ZIO[Any, FrameworkException, Unit] =
    this
      .delete(ElasticSearchConstants.SEQUENCE_INDEX, id)(
        authContext.systemNoSQLContext()
      )
      .unit

  def encodeBinary(data: Array[Byte]): String =
    new String(java.util.Base64.getMimeEncoder.encode(data))

  def decodeBinary(data: String): Array[Byte] =
    java.util.Base64.getMimeDecoder.decode(data)

  protected var bulkerStarted: Boolean = false

  private[elasticsearch] lazy val bulker =
    Bulker(this, bulkSize = config.bulkSize)

  def addToBulk(
    action: IndexRequest
  ): ZIO[Any, FrameworkException, IndexResponse] =
    for {
      blkr <- bulker
      _ <- blkr.add(action)
    } yield IndexResponse(
      index = action.index,
      id = action.id.getOrElse(""),
      version = 1
    )

  def addToBulk(
    action: DeleteRequest
  ): ZIO[Any, FrameworkException, DeleteResponse] =
    for {
      blkr <- bulker
      _ <- blkr.add(action)
    } yield DeleteResponse(action.index, action.id)

  def addToBulk(
    action: UpdateRequest
  ): ZIO[Any, FrameworkException, UpdateResponse] =
    for {
      blkr <- bulker
      _ <- blkr.add(action)
    } yield UpdateResponse(action.index, action.id)

  def executeBulk(body: String, async: Boolean = false): ZIO[Any, FrameworkException, BulkResponse] =
    if (body.nonEmpty) {
      this.bulk(body)
    } else ZIO.succeed(BulkResponse(0, false, Nil))

  def sink[T](
    index: String,
    bulkSize: Int = 500,
    flushInterval: Duration = 5.seconds,
    create: Boolean = false,
    idFunction: Option[IDFunction[T]] = None,
    indexFunction: Option[T => String] = None,
    pipeline: Option[String] = None,
    extra: Json.Obj = Json.Obj()
  )(implicit encoder: JsonEncoder[T]): ZSink[Any, Throwable, T, T, Long] = {

    val managedChannel = ZIO.acquireRelease {
      for {
        bulker <- Bulker(this, bulkSize = bulkSize, flushInterval = flushInterval)
      } yield bulker
    } { bulker =>
      (for {
        _ <- ZIO.logDebug(s"Flushing sink bulker: $index")
        _ <- bulker.flushBulk()
        _ <- ZIO.logDebug(s"Closing sink bulker: $index")
        _ <- bulker.close()
        _ <- ZIO.logDebug(s"Closed sink bulker: $index")
      } yield ()).ignore
    }

    // val writer: ZSink[Any, Throwable, T, T, Unit] = ZSink.unwrapScoped(bulkerSink)

    val writer: ZSink[Any, Throwable, T, T, Unit] =
      ZSink.foreachChunk[Any, Throwable, T] { byteChunk =>
        val items = byteChunk.map(
          o =>
            buildIndexRequest(
              index = index,
              item = o,
              idFunction = idFunction.getOrElse { _: T =>
                None
              },
              indexFunction = indexFunction,
              create = create,
              pipeline = pipeline,
              extra = extra
            )
        )
        for {
          bulker <- ZIO.scoped(managedChannel)
          _ <- ZIO.foreach(items)(i => bulker.add(i))
        } yield ()

      }

    writer &> ZSink.count

  }

  def buildIndexRequest[T](
    index: String,
    item: T,
    idFunction: IDFunction[T] = IdFunctions.typeToNone[T],
    indexFunction: Option[T => String] = None,
    create: Boolean = false,
    pipeline: Option[String] = None,
    extra: Json.Obj = Json.Obj()
  )(
    implicit
    enc: JsonEncoder[T]
  ): IndexRequest = {
    val id = item match {
      case c: CustomId => Some(c.calcId())
      case _           => idFunction(item)
    }

    val realIndex = item match {
      case c: CustomIndex               => c.calcIndex()
      case _ if indexFunction.isDefined => indexFunction.get.apply(item)
      case _                            => index
    }

    IndexRequest(
      index = realIndex,
      body =
        if (extra.fields.nonEmpty)
          Json.Obj(item.toJsonAST.getOrElse(Json.Obj()).asInstanceOf[Json.Obj].fields ++ extra.fields)
        else item.toJsonAST.map(_.asInstanceOf[Json.Obj]).getOrElse(Json.Obj()),
      id = id,
      opType =
        if (create) OpType.create
        else OpType.index,
      pipeline = pipeline
    )
  }

  def bulkIndex[T](
    index: String,
    items: Seq[T],
    idFunction: IDFunction[T] = IdFunctions.typeToNone[T],
    create: Boolean = false,
    pipeline: Option[String] = None,
    extra: Json.Obj = Json.Obj()
  )(
    implicit
    enc: JsonEncoder[T]
  ): ZIO[Any, FrameworkException, BulkResponse] =
    if (items.isEmpty) ZIO.succeed(BulkResponse(0, false, Nil))
    else {
      this.bulk(
        body = items
          .map(
            i =>
              buildIndexRequest[T](
                index = index,
                item = i,
                idFunction = idFunction,
                create = create,
                pipeline = pipeline,
                extra = extra
              ).toBulkString
          )
          .mkString
      )
    }

  def bulkDelete[T](
    index: String,
    items: Seq[T],
    idFunction: T => String
  ): ZIO[Any, FrameworkException, BulkResponse] =
    if (items.isEmpty) ZIO.succeed(BulkResponse(0, false, Nil))
    else {
      this.bulk(
        body = items
          .map(
            i =>
              DeleteRequest(
                index = index,
                id = idFunction(i)
              ).toBulkString
          )
          .mkString("\n")
      )
    }

  def bulk(actions: Seq[BulkActionRequest]): ZIO[Any, FrameworkException, BulkResponse] =
    if (actions.isEmpty)
      ZIO.succeed(BulkResponse(0, false, Nil))
    else {
      this.bulk(
        body = actions.map(_.toBulkString).mkString
      )
    }

  def bulkStream(
    actions: zio.stream.Stream[FrameworkException, BulkActionRequest],
    size: Int = 1000
  ): ZIO[Any, FrameworkException, Unit] =
    actions.grouped(size).foreach(b => bulk(b))

}
object ElasticSearchService {

  // services

  private case class Live(
    httpService: HTTPService,
    config: ElasticSearchConfig,
    applicationName: String
  ) extends ElasticSearchService

  val live: ZLayer[HTTPService, Nothing, ElasticSearchService] =
    ZLayer {
      for {
        httpService <- ZIO.service[HTTPService]
      } yield Live(
        httpService,
        httpService.elasticSearchConfig,
        httpService.elasticSearchConfig.applicationName.getOrElse("default")
      )
    }

  val fromElasticSearch: ZLayer[HTTPService with ElasticSearch, Nothing, ElasticSearchService] =
    ZLayer {
      for {
        httpService <- ZIO.service[HTTPService]
        elasticSearch <- ZIO.service[ElasticSearch]
        elasticSearchConfig <- elasticSearch.esConfig
      } yield Live(
        httpService,
        elasticSearchConfig,
        elasticSearchConfig.applicationName.getOrElse("default")
      )
    }

  def bulk(actions: Seq[BulkActionRequest]): ZIO[ElasticSearchService, FrameworkException, BulkResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.bulk(actions))

  def getSequenceValue(
    id: String,
    index: String = ElasticSearchConstants.SEQUENCE_INDEX,
    docType: String = "sequence"
  )(
    implicit
    authContext: AuthContext
  ): ZIO[ElasticSearchService, FrameworkException, Option[Long]] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.getSequenceValue(id, index, docType))

  /* Reset the sequence for the id */
  def resetSequence(
    id: String
  )(implicit authContext: AuthContext): ZIO[ElasticSearchService, FrameworkException, Unit] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.resetSequence(id))

  def encodeBinary(data: Array[Byte]): String =
    new String(java.util.Base64.getMimeEncoder.encode(data))

  def decodeBinary(data: String): Array[Byte] =
    java.util.Base64.getMimeDecoder.decode(data)

  def addToBulk(
    action: IndexRequest
  ): ZIO[ElasticSearchService, FrameworkException, IndexResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.addToBulk(action))

  def addToBulk(
    action: DeleteRequest
  ): ZIO[ElasticSearchService, FrameworkException, DeleteResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.addToBulk(action))
  def addToBulk(
    action: UpdateRequest
  ): ZIO[ElasticSearchService, FrameworkException, UpdateResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.addToBulk(action))
  def executeBulk(body: String, async: Boolean = false): ZIO[ElasticSearchService, FrameworkException, BulkResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.executeBulk(body, async))

  def bulkIndex[T](
    index: String,
    items: Seq[T],
    idFunction: IDFunction[T] = IdFunctions.typeToNone[T],
    create: Boolean = false
  )(
    implicit
    enc: JsonEncoder[T]
  ): ZIO[ElasticSearchService, FrameworkException, BulkResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.bulkIndex[T](index, items, idFunction, create))

  def bulkDelete[T](
    index: String,
    items: Seq[T],
    idFunction: T => String
  ): ZIO[ElasticSearchService, FrameworkException, BulkResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.bulkDelete[T](index, items, idFunction))

  def bulkStream(
    actions: zio.stream.Stream[FrameworkException, BulkActionRequest],
    size: Int = 1000
  ): ZIO[ElasticSearchService, FrameworkException, Unit] =
    actions.grouped(size).foreach(b => bulk(b))

  // extended client manager
  def searchScroll(
    scrollId: String
  ): ZIO[ElasticSearchService, FrameworkException, SearchResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.searchScroll(scrollId))

  def searchScroll(
    scrollId: String,
    keepAlive: String
  ): ZIO[ElasticSearchService, FrameworkException, SearchResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.searchScroll(scrollId, keepAlive))

  def searchScrollTyped[T: JsonEncoder: JsonDecoder](
    scrollId: String,
    keepAlive: String
  ): ZIO[ElasticSearchService, FrameworkException, SearchResult[T]] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.searchScrollTyped[T](scrollId, keepAlive))

  // client manager

  /*
   * Allows to perform multiple index/update/delete operations in a single request.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-bulk.html
   *
   * @param body body the body of the call
   * @param docType Default document type for items which don't provide one
   * @param index Default index for items which don't provide one
   * @param pipeline The pipeline id to preprocess incoming documents with
   * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param routing Specific routing value
   * @param source True or false to return the _source field or not, or default list of fields to return, can be overridden on each sub-request
   * @param sourceExcludes Default list of fields to exclude from the returned _source field, can be overridden on each sub-request
   * @param sourceIncludes Default list of fields to extract and return from the _source field, can be overridden on each sub-request
   * @param timeout Explicit operation timeout
   * @param `type` Default document type for items which don't provide one
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the bulk operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def bulk(
    body: String,
    index: Option[String] = None,
    pipeline: Option[String] = None,
    refresh: Option[Refresh] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    timeout: Option[String] = None,
    `type`: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[ElasticSearchService, FrameworkException, BulkResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.bulk(
        body = body,
        index = index,
        pipeline = pipeline,
        refresh = refresh,
        routing = routing,
        source = source,
        sourceExcludes = sourceExcludes,
        sourceIncludes = sourceIncludes,
        timeout = timeout,
        `type` = `type`,
        waitForActiveShards = waitForActiveShards
      )
    )

  def bulk(request: BulkRequest): ZIO[ElasticSearchService, FrameworkException, BulkResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Explicitly clears the search context for a scroll.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-body.html#_clear_scroll_api
   *
   * @param body body the body of the call
   */
//  def clearScroll(body: Option[Json.Obj] = None): ZIO[ElasticSearchService, FrameworkException, ClearScrollResponse] =
//    ZIO.environmentWithZIO[ElasticSearchService](_.get.clearScroll(body = body))

  def clearScroll(request: ClearScrollRequest): ZIO[ElasticSearchService, FrameworkException, ClearScrollResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns number of documents matching a query.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-count.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
   * @param analyzer The analyzer to use for the query string
   * @param body body the body of the call
   * @param defaultOperator The default operator for query string query (AND or OR)
   * @param df The field to use as default where no field prefix is given in the query string
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreThrottled Whether specified concrete, expanded or aliased indices should be ignored when throttled
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of indices to restrict the results
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param minScore Include only documents with a specific `_score` value in the result
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param q Query in the Lucene query string syntax
   * @param routing A comma-separated list of specific routing values
   * @param terminateAfter The maximum count for each shard, upon reaching which the query execution will terminate early
   */
  def count(
    indices: Seq[String],
    body: Json.Obj = Json.Obj(),
    allowNoIndices: Option[Boolean] = None,
    analyzeWildcard: Option[Boolean] = None,
    analyzer: Option[String] = None,
    defaultOperator: DefaultOperator = DefaultOperator.OR,
    df: Option[String] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreThrottled: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    lenient: Option[Boolean] = None,
    minScore: Option[Double] = None,
    preference: Option[String] = None,
    q: Option[String] = None,
    routing: Seq[String] = Nil,
    terminateAfter: Option[Long] = None
  ): ZIO[ElasticSearchService, FrameworkException, CountResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.count(
        allowNoIndices = allowNoIndices,
        analyzeWildcard = analyzeWildcard,
        analyzer = analyzer,
        body = body,
        defaultOperator = defaultOperator,
        df = df,
        expandWildcards = expandWildcards,
        ignoreThrottled = ignoreThrottled,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        lenient = lenient,
        minScore = minScore,
        preference = preference,
        q = q,
        routing = routing,
        terminateAfter = terminateAfter
      )
    )

  def count(request: CountRequest): ZIO[ElasticSearchService, FrameworkException, CountResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Creates a new document in the index.

Returns a 409 response when a document with a same ID already exists in the index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-index_.html
   *
   * @param index The name of the index
   * @param id Document ID
   * @param body body the body of the call
   * @param pipeline The pipeline id to preprocess incoming documents with
   * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param routing Specific routing value
   * @param timeout Explicit operation timeout
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the index operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def create(
    index: String,
    id: String,
    body: Json.Obj,
    pipeline: Option[String] = None,
    refresh: Option[Refresh] = None,
    routing: Option[String] = None,
    timeout: Option[String] = None,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[ElasticSearchService, FrameworkException, CreateResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.create(
        index = index,
        id = id,
        body = body,
        pipeline = pipeline,
        refresh = refresh,
        routing = routing,
        timeout = timeout,
        version = version,
        versionType = versionType,
        waitForActiveShards = waitForActiveShards
      )
    )

  def create(request: CreateRequest): ZIO[ElasticSearchService, FrameworkException, CreateResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Removes a document from the index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-delete.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param ifPrimaryTerm only perform the delete operation if the last operation that has changed the document has the specified primary term
   * @param ifSeqNo only perform the delete operation if the last operation that has changed the document has the specified sequence number
   * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param routing Specific routing value
   * @param timeout Explicit operation timeout
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the delete operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def delete(
    index: String,
    id: String,
    ifPrimaryTerm: Option[Double] = None,
    ifSeqNo: Option[Double] = None,
    refresh: Option[Refresh] = None,
    routing: Option[String] = None,
    timeout: Option[String] = None,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None,
    waitForActiveShards: Option[String] = None
  )(implicit authContext: AuthContext): ZIO[ElasticSearchService, FrameworkException, DeleteResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.delete(
        index = index,
        id = id,
        ifPrimaryTerm = ifPrimaryTerm,
        ifSeqNo = ifSeqNo,
        refresh = refresh,
        routing = routing,
        timeout = timeout,
        version = version,
        versionType = versionType,
        waitForActiveShards = waitForActiveShards
      )
    )

  def delete(request: DeleteRequest): ZIO[ElasticSearchService, FrameworkException, DeleteResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Deletes documents matching the provided query.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-delete-by-query.html
   *
   * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param body body the body of the call
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
   * @param analyzer The analyzer to use for the query string
   * @param conflicts What to do when the delete by query hits version conflicts?
   * @param defaultOperator The default operator for query string query (AND or OR)
   * @param df The field to use as default where no field prefix is given in the query string
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param from Starting offset (default: 0)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param maxDocs Maximum number of documents to process (default: all documents)
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param q Query in the Lucene query string syntax
   * @param refresh Should the effected indexes be refreshed?
   * @param requestCache Specify if request cache should be used for this request or not, defaults to index level setting
   * @param requestsPerSecond The throttle for this request in sub-requests per second. -1 means no throttle.
   * @param routing A comma-separated list of specific routing values
   * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
   * @param scrollSize Size on the scroll request powering the delete by query
   * @param searchTimeout Explicit timeout for each search request. Defaults to no timeout.
   * @param searchType Search operation type
   * @param slices The number of slices this task should be divided into. Defaults to 1 meaning the task isn't sliced into subtasks.
   * @param sort A comma-separated list of <field>:<direction> pairs
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param sourceExcludes A list of fields to exclude from the returned _source field
   * @param sourceIncludes A list of fields to extract and return from the _source field
   * @param stats Specific 'tag' of the request for logging and statistical purposes
   * @param terminateAfter The maximum number of documents to collect for each shard, upon reaching which the query execution will terminate early.
   * @param timeout Time each individual bulk request should wait for shards that are unavailable.
   * @param version Specify whether to return document version as part of a hit
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the delete by query operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   * @param waitForCompletion Should the request should block until the delete by query is complete.
   */
  def deleteByQuery(
    indices: Seq[String] = Nil,
    body: Json.Obj,
    allowNoIndices: Option[Boolean] = None,
    analyzeWildcard: Option[Boolean] = None,
    analyzer: Option[String] = None,
    conflicts: Seq[Conflicts] = Nil,
    defaultOperator: DefaultOperator = DefaultOperator.OR,
    df: Option[String] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    from: Option[Double] = None,
    ignoreUnavailable: Option[Boolean] = None,
    lenient: Option[Boolean] = None,
    maxDocs: Option[Double] = None,
    preference: Option[String] = None,
    q: Option[String] = None,
    refresh: Option[Boolean] = None,
    requestCache: Option[Boolean] = None,
    requestsPerSecond: Int = 0,
    routing: Seq[String] = Nil,
    scroll: Option[String] = None,
    scrollSize: Option[Double] = None,
    searchTimeout: Option[String] = None,
    searchType: Option[SearchType] = None,
    slices: Double = 1,
    sort: Seq[String] = Nil,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    stats: Seq[String] = Nil,
    terminateAfter: Option[Long] = None,
    timeout: String = "1m",
    version: Option[Boolean] = None,
    waitForActiveShards: Option[String] = None,
    waitForCompletion: Boolean = true
  ): ZIO[ElasticSearchService, FrameworkException, DeleteByQueryResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.deleteByQuery(
        indices = indices,
        body = body,
        allowNoIndices = allowNoIndices,
        analyzeWildcard = analyzeWildcard,
        analyzer = analyzer,
        conflicts = conflicts,
        defaultOperator = defaultOperator,
        df = df,
        expandWildcards = expandWildcards,
        from = from,
        ignoreUnavailable = ignoreUnavailable,
        lenient = lenient,
        maxDocs = maxDocs,
        preference = preference,
        q = q,
        refresh = refresh,
        requestCache = requestCache,
        requestsPerSecond = requestsPerSecond,
        routing = routing,
        scroll = scroll,
        scrollSize = scrollSize,
        searchTimeout = searchTimeout,
        searchType = searchType,
        slices = slices,
        sort = sort,
        source = source,
        sourceExcludes = sourceExcludes,
        sourceIncludes = sourceIncludes,
        stats = stats,
        terminateAfter = terminateAfter,
        timeout = timeout,
        version = version,
        waitForActiveShards = waitForActiveShards,
        waitForCompletion = waitForCompletion
      )
    )

  def deleteByQuery(
    request: DeleteByQueryRequest
  ): ZIO[ElasticSearchService, FrameworkException, DeleteByQueryResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Changes the number of requests per second for a particular Delete By Query operation.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete-by-query.html
   *
   * @param requestsPerSecond The throttle to set on this request in floating sub-requests per second. -1 means set no throttle.
   * @param taskId The task id to rethrottle
   */
  def deleteByQueryRethrottle(
    requestsPerSecond: Int,
    taskId: String
  ): ZIO[ElasticSearchService, FrameworkException, DeleteByQueryRethrottleResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.deleteByQueryRethrottle(requestsPerSecond = requestsPerSecond, taskId = taskId)
    )

  def deleteByQueryRethrottle(
    request: DeleteByQueryRethrottleRequest
  ): ZIO[ElasticSearchService, FrameworkException, DeleteByQueryRethrottleResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Deletes a script.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
   *
   * @param id Script ID
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def deleteScript(
    id: String,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[ElasticSearchService, FrameworkException, DeleteScriptResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.deleteScript(id = id, masterTimeout = masterTimeout, timeout = timeout)
    )

  def deleteScript(request: DeleteScriptRequest): ZIO[ElasticSearchService, FrameworkException, DeleteScriptResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns information about whether a document exists in an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param realtime Specify whether to perform the operation in realtime or search mode
   * @param refresh Refresh the shard containing the document before performing the operation
   * @param routing Specific routing value
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param sourceExcludes A list of fields to exclude from the returned _source field
   * @param sourceIncludes A list of fields to extract and return from the _source field
   * @param storedFields A comma-separated list of stored fields to return in the response
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   */
  def exists(
    index: String,
    id: String,
    preference: Option[String] = None,
    realtime: Option[Boolean] = None,
    refresh: Option[Boolean] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    storedFields: Seq[String] = Nil,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None
  ): ZIO[ElasticSearchService, FrameworkException, ExistsResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.exists(
        index = index,
        id = id,
        preference = preference,
        realtime = realtime,
        refresh = refresh,
        routing = routing,
        source = source,
        sourceExcludes = sourceExcludes,
        sourceIncludes = sourceIncludes,
        storedFields = storedFields,
        version = version,
        versionType = versionType
      )
    )

  def exists(request: ExistsRequest): ZIO[ElasticSearchService, FrameworkException, ExistsResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns information about whether a document source exists in an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param realtime Specify whether to perform the operation in realtime or search mode
   * @param refresh Refresh the shard containing the document before performing the operation
   * @param routing Specific routing value
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param sourceExcludes A list of fields to exclude from the returned _source field
   * @param sourceIncludes A list of fields to extract and return from the _source field
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   */
  def existsSource(
    index: String,
    id: String,
    preference: Option[String] = None,
    realtime: Option[Boolean] = None,
    refresh: Option[Boolean] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None
  ): ZIO[ElasticSearchService, FrameworkException, ExistsSourceResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.existsSource(
        index = index,
        id = id,
        preference = preference,
        realtime = realtime,
        refresh = refresh,
        routing = routing,
        source = source,
        sourceExcludes = sourceExcludes,
        sourceIncludes = sourceIncludes,
        version = version,
        versionType = versionType
      )
    )

  def existsSource(request: ExistsSourceRequest): ZIO[ElasticSearchService, FrameworkException, ExistsSourceResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns information about why a specific matches (or doesn't match) a query.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-explain.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param analyzeWildcard Specify whether wildcards and prefix queries in the query string query should be analyzed (default: false)
   * @param analyzer The analyzer for the query string query
   * @param body body the body of the call
   * @param defaultOperator The default operator for query string query (AND or OR)
   * @param df The default field for query string query (default: _all)
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param q Query in the Lucene query string syntax
   * @param routing Specific routing value
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param sourceExcludes A list of fields to exclude from the returned _source field
   * @param sourceIncludes A list of fields to extract and return from the _source field
   * @param storedFields A comma-separated list of stored fields to return in the response
   */
  def explain(
    index: String,
    id: String,
    body: Json.Obj,
    analyzeWildcard: Option[Boolean] = None,
    analyzer: Option[String] = None,
    defaultOperator: DefaultOperator = DefaultOperator.OR,
    df: Option[String] = None,
    lenient: Option[Boolean] = None,
    preference: Option[String] = None,
    q: Option[String] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    storedFields: Seq[String] = Nil
  ): ZIO[ElasticSearchService, FrameworkException, ExplainResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.explain(
        index = index,
        id = id,
        analyzeWildcard = analyzeWildcard,
        analyzer = analyzer,
        body = body,
        defaultOperator = defaultOperator,
        df = df,
        lenient = lenient,
        preference = preference,
        q = q,
        routing = routing,
        source = source,
        sourceExcludes = sourceExcludes,
        sourceIncludes = sourceIncludes,
        storedFields = storedFields
      )
    )

  def explain(request: ExplainRequest): ZIO[ElasticSearchService, FrameworkException, ExplainResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns the information about the capabilities of fields among multiple indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-field-caps.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param fields A comma-separated list of field names
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param includeUnmapped Indicates whether unmapped fields should be included in the response.
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   */
  def fieldCaps(
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    fields: Seq[String] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    includeUnmapped: Boolean = false,
    indices: Seq[String] = Nil
  ): ZIO[ElasticSearchService, FrameworkException, FieldCapsResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.fieldCaps(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        fields = fields,
        ignoreUnavailable = ignoreUnavailable,
        includeUnmapped = includeUnmapped,
        indices = indices
      )
    )

  def fieldCaps(request: FieldCapsRequest): ZIO[ElasticSearchService, FrameworkException, FieldCapsResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns a document.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param realtime Specify whether to perform the operation in realtime or search mode
   * @param refresh Refresh the shard containing the document before performing the operation
   * @param routing Specific routing value
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param sourceExclude A list of fields to exclude from the returned _source field
   * @param sourceInclude A list of fields to extract and return from the _source field
   * @param storedFields A comma-separated list of stored fields to return in the response
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   */
  def get(
    index: String,
    id: String,
    preference: Option[String] = None,
    realtime: Option[Boolean] = None,
    refresh: Option[Boolean] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExclude: Seq[String] = Nil,
    sourceInclude: Seq[String] = Nil,
    storedFields: Seq[String] = Nil,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None
  )(implicit authContext: AuthContext): ZIO[ElasticSearchService, FrameworkException, GetResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.get(
        index = index,
        id = id,
        preference = preference,
        realtime = realtime,
        refresh = refresh,
        routing = routing,
        source = source,
        sourceExclude = sourceExclude,
        sourceInclude = sourceInclude,
        storedFields = storedFields,
        version = version,
        versionType = versionType
      )
    )

  def get(request: GetRequest): ZIO[ElasticSearchService, FrameworkException, GetResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns a script.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
   *
   * @param id Script ID
   * @param masterTimeout Specify timeout for connection to master
   */
  def getScript(
    id: String,
    masterTimeout: Option[String] = None
  ): ZIO[ElasticSearchService, FrameworkException, GetScriptResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.getScript(id = id, masterTimeout = masterTimeout))

  def getScript(request: GetScriptRequest): ZIO[ElasticSearchService, FrameworkException, GetScriptResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns the source of a document.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param realtime Specify whether to perform the operation in realtime or search mode
   * @param refresh Refresh the shard containing the document before performing the operation
   * @param routing Specific routing value
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param sourceExcludes A list of fields to exclude from the returned _source field
   * @param sourceIncludes A list of fields to extract and return from the _source field
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   */
  def getSource(
    index: String,
    id: String,
    preference: Option[String] = None,
    realtime: Option[Boolean] = None,
    refresh: Option[Boolean] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None
  ): ZIO[ElasticSearchService, FrameworkException, GetSourceResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.getSource(
        index = index,
        id = id,
        preference = preference,
        realtime = realtime,
        refresh = refresh,
        routing = routing,
        source = source,
        sourceExcludes = sourceExcludes,
        sourceIncludes = sourceIncludes,
        version = version,
        versionType = versionType
      )
    )

  def getSource(request: GetSourceRequest): ZIO[ElasticSearchService, FrameworkException, GetSourceResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Creates or updates a document in an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-index_.html
   *
   * @param index The name of the index
   * @param id Document ID
   * @param body body the body of the call
   * @param ifPrimaryTerm only perform the index operation if the last operation that has changed the document has the specified primary term
   * @param ifSeqNo only perform the index operation if the last operation that has changed the document has the specified sequence number
   * @param opType Explicit operation type. Defaults to `index` for requests with an explicit document ID, and to `create`for requests without an explicit document ID
   * @param pipeline The pipeline id to preprocess incoming documents with
   * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param routing Specific routing value
   * @param timeout Explicit operation timeout
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the index operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def indexDocument(
    index: String,
    body: Json.Obj,
    id: Option[String] = None,
    ifPrimaryTerm: Option[Double] = None,
    ifSeqNo: Option[Double] = None,
    opType: OpType = OpType.index,
    pipeline: Option[String] = None,
    refresh: Option[Refresh] = None,
    routing: Option[String] = None,
    timeout: Option[String] = None,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None,
    waitForActiveShards: Option[Int] = None
  )(implicit authContext: AuthContext): ZIO[ElasticSearchService, FrameworkException, IndexResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.indexDocument(
        index = index,
        body = body,
        id = id,
        ifPrimaryTerm = ifPrimaryTerm,
        ifSeqNo = ifSeqNo,
        opType = opType,
        pipeline = pipeline,
        refresh = refresh,
        routing = routing,
        timeout = timeout,
        version = version,
        versionType = versionType,
        waitForActiveShards = waitForActiveShards
      )
    )

  def index(request: IndexRequest): ZIO[ElasticSearchService, FrameworkException, IndexResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns basic information about the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html
   *

   */
  def info(): ZIO[ElasticSearchService, FrameworkException, InfoResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.info())

  def info(request: InfoRequest): ZIO[ElasticSearchService, FrameworkException, InfoResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Allows to get multiple documents in one request.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-multi-get.html
   *
   * @param body body the body of the call
   * @param index The name of the index
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param realtime Specify whether to perform the operation in realtime or search mode
   * @param refresh Refresh the shard containing the document before performing the operation
   * @param routing Specific routing value
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param sourceExcludes A list of fields to exclude from the returned _source field
   * @param sourceIncludes A list of fields to extract and return from the _source field
   * @param storedFields A comma-separated list of stored fields to return in the response
   */
  def mget(
    body: Seq[(String, String)],
    index: Option[String] = None,
    preference: Option[String] = None,
    realtime: Option[Boolean] = None,
    refresh: Option[Boolean] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    storedFields: Seq[String] = Nil
  ): ZIO[ElasticSearchService, FrameworkException, MultiGetResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.mget(
        body = body,
        index = index,
        preference = preference,
        realtime = realtime,
        refresh = refresh,
        routing = routing,
        source = source,
        sourceExcludes = sourceExcludes,
        sourceIncludes = sourceIncludes,
        storedFields = storedFields
      )
    )

  def mget(request: MultiGetRequest): ZIO[ElasticSearchService, FrameworkException, MultiGetResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Allows to execute several search operations in one request.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-multi-search.html
   *
   * @param body body the body of the call
   * @param ccsMinimizeRoundtrips Indicates whether network round-trips should be minimized as part of cross-cluster search requests execution
   * @param indices A comma-separated list of index names to use as default
   * @param maxConcurrentSearches Controls the maximum number of concurrent searches the multi search api will execute
   * @param maxConcurrentShardRequests The number of concurrent shard requests each sub search executes concurrently per node. This value should be used to limit the impact of the search on the cluster in order to limit the number of concurrent shard requests
   * @param preFilterShardSize A threshold that enforces a pre-filter roundtrip to prefilter search shards based on query rewriting if the number of shards the search request expands to exceeds the threshold. This filter roundtrip can limit the number of shards significantly if for instance a shard can not match any documents based on it's rewrite method ie. if date filters are mandatory to match but the shard bounds and the query are disjoint.
   * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
   * @param searchType Search operation type
   * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
   */
  def msearch(
    body: Seq[String] = Nil,
    ccsMinimizeRoundtrips: Boolean = true,
    indices: Seq[String] = Nil,
    maxConcurrentSearches: Option[Double] = None,
    maxConcurrentShardRequests: Double = 5,
    preFilterShardSize: Double = 128,
    restTotalHitsAsInt: Boolean = false,
    searchType: Option[SearchType] = None,
    typedKeys: Option[Boolean] = None
  ): ZIO[ElasticSearchService, FrameworkException, MultiSearchResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.msearch(
        body = body,
        ccsMinimizeRoundtrips = ccsMinimizeRoundtrips,
        indices = indices,
        maxConcurrentSearches = maxConcurrentSearches,
        maxConcurrentShardRequests = maxConcurrentShardRequests,
        preFilterShardSize = preFilterShardSize,
        restTotalHitsAsInt = restTotalHitsAsInt,
        searchType = searchType,
        typedKeys = typedKeys
      )
    )

  def msearch(request: MultiSearchRequest): ZIO[ElasticSearchService, FrameworkException, MultiSearchResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Allows to execute several search template operations in one request.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-multi-search.html
   *
   * @param body body the body of the call
   * @param ccsMinimizeRoundtrips Indicates whether network round-trips should be minimized as part of cross-cluster search requests execution
   * @param indices A comma-separated list of index names to use as default
   * @param maxConcurrentSearches Controls the maximum number of concurrent searches the multi search api will execute
   * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
   * @param searchType Search operation type
   * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
   */
  def msearchTemplate(
    body: Seq[String] = Nil,
    ccsMinimizeRoundtrips: Boolean = true,
    indices: Seq[String] = Nil,
    maxConcurrentSearches: Option[Double] = None,
    restTotalHitsAsInt: Boolean = false,
    searchType: Option[SearchType] = None,
    typedKeys: Option[Boolean] = None
  ): ZIO[ElasticSearchService, FrameworkException, MsearchTemplateResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.msearchTemplate(
        body = body,
        ccsMinimizeRoundtrips = ccsMinimizeRoundtrips,
        indices = indices,
        maxConcurrentSearches = maxConcurrentSearches,
        restTotalHitsAsInt = restTotalHitsAsInt,
        searchType = searchType,
        typedKeys = typedKeys
      )
    )

  def msearchTemplate(
    request: MsearchTemplateRequest
  ): ZIO[ElasticSearchService, FrameworkException, MsearchTemplateResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns multiple termvectors in one request.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-multi-termvectors.html
   *
   * @param body body the body of the call
   * @param fieldStatistics Specifies if document count, sum of document frequencies and sum of total term frequencies should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param fields A comma-separated list of fields to return. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param ids A comma-separated list of documents ids. You must define ids as parameter or set "ids" or "docs" in the request body
   * @param index The index in which the document resides.
   * @param offsets Specifies if term offsets should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param payloads Specifies if term payloads should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param positions Specifies if term positions should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param preference Specify the node or shard the operation should be performed on (default: random) .Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param realtime Specifies if requests are real-time as opposed to near-real-time (default: true).
   * @param routing Specific routing value. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param termStatistics Specifies if total term frequency and document frequency should be returned. Applies to all returned documents unless otherwise specified in body "params" or "docs".
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   */
  def mtermvectors(
    body: Option[Json.Obj] = None,
    fieldStatistics: Boolean = true,
    fields: Seq[String] = Nil,
    ids: Seq[String] = Nil,
    index: Option[String] = None,
    offsets: Boolean = true,
    payloads: Boolean = true,
    positions: Boolean = true,
    preference: Option[String] = None,
    realtime: Option[Boolean] = None,
    routing: Option[String] = None,
    termStatistics: Boolean = false,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None
  ): ZIO[ElasticSearchService, FrameworkException, MultiTermVectorsResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.mtermvectors(
        body = body,
        fieldStatistics = fieldStatistics,
        fields = fields,
        ids = ids,
        index = index,
        offsets = offsets,
        payloads = payloads,
        positions = positions,
        preference = preference,
        realtime = realtime,
        routing = routing,
        termStatistics = termStatistics,
        version = version,
        versionType = versionType
      )
    )

  def mtermvectors(
    request: MultiTermVectorsRequest
  ): ZIO[ElasticSearchService, FrameworkException, MultiTermVectorsResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns whether the cluster is running.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html
   *

   */
  def ping(): ZIO[ElasticSearchService, FrameworkException, PingResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.ping())

  def ping(request: PingRequest): ZIO[ElasticSearchService, FrameworkException, PingResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Creates or updates a script.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
   *
   * @param id Script ID
   * @param body body the body of the call
   * @param context Context name to compile script against
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def putScript(
    id: String,
    body: Json.Obj,
    context: Option[String] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[ElasticSearchService, FrameworkException, PutScriptResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.putScript(id = id, body = body, context = context, masterTimeout = masterTimeout, timeout = timeout)
    )

  def putScript(request: PutScriptRequest): ZIO[ElasticSearchService, FrameworkException, PutScriptResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Allows to evaluate the quality of ranked search results over a set of typical search queries
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-rank-eval.html
   *
   * @param body body the body of the call
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
   */
  def rankEval(
    body: Json.Obj,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil
  ): ZIO[ElasticSearchService, FrameworkException, RankEvalResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.rankEval(
        body = body,
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices
      )
    )

  def rankEval(request: RankEvalRequest): ZIO[ElasticSearchService, FrameworkException, RankEvalResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Allows to copy documents from one index to another, optionally filtering the source
documents by a query, changing the destination index settings, or fetching the
documents from a remote cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-reindex.html
   *
   * @param body body the body of the call
   * @param maxDocs Maximum number of documents to process (default: all documents)
   * @param refresh Should the effected indexes be refreshed?
   * @param requestsPerSecond The throttle to set on this request in sub-requests per second. -1 means no throttle.
   * @param scroll Control how long to keep the search context alive
   * @param slices The number of slices this task should be divided into. Defaults to 1 meaning the task isn't sliced into subtasks.
   * @param timeout Time each individual bulk request should wait for shards that are unavailable.
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the reindex operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   * @param waitForCompletion Should the request should block until the reindex is complete.
   */
  def reindex(
    body: Json.Obj,
    maxDocs: Option[Double] = None,
    refresh: Option[Boolean] = None,
    requestsPerSecond: Int = 0,
    scroll: String = "5m",
    slices: Double = 1,
    timeout: String = "1m",
    waitForActiveShards: Option[String] = None,
    waitForCompletion: Boolean = true
  ): ZIO[ElasticSearchService, FrameworkException, ReindexResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.reindex(
        body = body,
        maxDocs = maxDocs,
        refresh = refresh,
        requestsPerSecond = requestsPerSecond,
        scroll = scroll,
        slices = slices,
        timeout = timeout,
        waitForActiveShards = waitForActiveShards,
        waitForCompletion = waitForCompletion
      )
    )

  def reindex(request: ReindexRequest): ZIO[ElasticSearchService, FrameworkException, ReindexResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Changes the number of requests per second for a particular Reindex operation.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-reindex.html
   *
   * @param requestsPerSecond The throttle to set on this request in floating sub-requests per second. -1 means set no throttle.
   * @param taskId The task id to rethrottle
   */
  def reindexRethrottle(
    requestsPerSecond: Int,
    taskId: String
  ): ZIO[ElasticSearchService, FrameworkException, ReindexRethrottleResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.reindexRethrottle(requestsPerSecond = requestsPerSecond, taskId = taskId)
    )

  def reindexRethrottle(
    request: ReindexRethrottleRequest
  ): ZIO[ElasticSearchService, FrameworkException, ReindexRethrottleResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Allows to use the Mustache language to pre-render a search definition.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-template.html#_validating_templates
   *
   * @param body body the body of the call
   * @param id The id of the stored search template
   */
  def renderSearchTemplate(
    body: Json.Obj,
    id: Option[String] = None
  ): ZIO[ElasticSearchService, FrameworkException, RenderSearchTemplateResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.renderSearchTemplate(body = body, id = id))

  def renderSearchTemplate(
    request: RenderSearchTemplateRequest
  ): ZIO[ElasticSearchService, FrameworkException, RenderSearchTemplateResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Allows an arbitrary script to be executed and a result to be returned
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/painless/master/painless-execute-api.html
   *
   * @param body body the body of the call
   */
  def scriptsPainlessExecute(
    body: Json.Obj
  ): ZIO[ElasticSearchService, FrameworkException, ScriptsPainlessExecuteResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.scriptsPainlessExecute(body = body))

  def scriptsPainlessExecute(
    request: ScriptsPainlessExecuteRequest
  ): ZIO[ElasticSearchService, FrameworkException, ScriptsPainlessExecuteResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Allows to retrieve a large numbers of results from a single search request.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-body.html#request-body-search-scroll
   *
   * @param body body the body of the call
   * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
   * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
   * @param scrollId The scroll ID for scrolled search
   */
  def scroll(
    scrollId: String,
    restTotalHitsAsInt: Boolean = false,
    scroll: Option[String] = None
  ): ZIO[ElasticSearchService, FrameworkException, SearchResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.scroll(restTotalHitsAsInt = restTotalHitsAsInt, scroll = scroll, scrollId = scrollId)
    )

  def scroll(request: ScrollRequest): ZIO[ElasticSearchService, FrameworkException, SearchResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns results matching a query.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-search.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param allowPartialSearchResults Indicate if an error should be returned if there is a partial search failure or timeout
   * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
   * @param analyzer The analyzer to use for the query string
   * @param batchedReduceSize The number of shard results that should be reduced at once on the coordinating node. This value should be used as a protection mechanism to reduce the memory overhead per search request if the potential number of shards in the request can be large.
   * @param body body the body of the call
   * @param ccsMinimizeRoundtrips Indicates whether network round-trips should be minimized as part of cross-cluster search requests execution
   * @param defaultOperator The default operator for query string query (AND or OR)
   * @param df The field to use as default where no field prefix is given in the query string
   * @param docvalueFields A comma-separated list of fields to return as the docvalue representation of a field for each hit
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param explain Specify whether to return detailed information about score computation as part of a hit
   * @param from Starting offset (default: 0)
   * @param ignoreThrottled Whether specified concrete, expanded or aliased indices should be ignored when throttled
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param maxConcurrentShardRequests The number of concurrent shard requests per node this search executes concurrently. This value should be used to limit the impact of the search on the cluster in order to limit the number of concurrent shard requests
   * @param preFilterShardSize A threshold that enforces a pre-filter roundtrip to prefilter search shards based on query rewriting if the number of shards the search request expands to exceeds the threshold. This filter roundtrip can limit the number of shards significantly if for instance a shard can not match any documents based on it's rewrite method ie. if date filters are mandatory to match but the shard bounds and the query are disjoint.
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param q Query in the Lucene query string syntax
   * @param requestCache Specify if request cache should be used for this request or not, defaults to index level setting
   * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
   * @param routing A comma-separated list of specific routing values
   * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
   * @param searchType Search operation type
   * @param seqNoPrimaryTerm Specify whether to return sequence number and primary term of the last modification of each hit
   * @param size Number of hits to return (default: 10)
   * @param sort A comma-separated list of <field>:<direction> pairs
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param sourceExcludes A list of fields to exclude from the returned _source field
   * @param sourceIncludes A list of fields to extract and return from the _source field
   * @param stats Specific 'tag' of the request for logging and statistical purposes
   * @param storedFields A comma-separated list of stored fields to return as part of a hit
   * @param suggestField Specify which field to use for suggestions
   * @param suggestMode Specify suggest mode
   * @param suggestSize How many suggestions to return in response
   * @param suggestText The source text for which the suggestions should be returned
   * @param terminateAfter The maximum number of documents to collect for each shard, upon reaching which the query execution will terminate early.
   * @param timeout Explicit operation timeout
   * @param trackScores Whether to calculate and return scores even if they are not used for sorting
   * @param trackTotalHits Indicate if the number of documents that match the query should be tracked
   * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
   * @param version Specify whether to return document version as part of a hit
   */
  def search(
    body: Json,
    allowNoIndices: Option[Boolean] = None,
    allowPartialSearchResults: Boolean = true,
    analyzeWildcard: Option[Boolean] = None,
    analyzer: Option[String] = None,
    batchedReduceSize: Int = 512,
    ccsMinimizeRoundtrips: Boolean = true,
    defaultOperator: DefaultOperator = DefaultOperator.OR,
    df: Option[String] = None,
    docvalueFields: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    explain: Option[Boolean] = None,
    from: Option[Long] = None,
    ignoreThrottled: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    lenient: Option[Boolean] = None,
    maxConcurrentShardRequests: Int = 5,
    preFilterShardSize: Int = 128,
    preference: Option[String] = None,
    q: Option[String] = None,
    requestCache: Option[Boolean] = None,
    restTotalHitsAsInt: Boolean = false,
    routing: Seq[String] = Nil,
    scroll: Option[String] = None,
    searchType: Option[SearchType] = None,
    seqNoPrimaryTerm: Option[Boolean] = None,
    size: Option[Long] = None,
    sort: Seq[String] = Nil,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    stats: Seq[String] = Nil,
    storedFields: Seq[String] = Nil,
    suggestField: Option[String] = None,
    suggestMode: SuggestMode = SuggestMode.missing,
    suggestSize: Option[Int] = None,
    suggestText: Option[String] = None,
    terminateAfter: Option[Long] = None,
    timeout: Option[String] = None,
    trackScores: Option[Boolean] = None,
    trackTotalHits: Option[Boolean] = None,
    typedKeys: Option[Boolean] = None,
    version: Option[Boolean] = None
  ): ZIO[ElasticSearchService, FrameworkException, SearchResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.searchRaw(
        body = body,
        allowNoIndices = allowNoIndices,
        allowPartialSearchResults = allowPartialSearchResults,
        analyzeWildcard = analyzeWildcard,
        analyzer = analyzer,
        batchedReduceSize = batchedReduceSize,
        ccsMinimizeRoundtrips = ccsMinimizeRoundtrips,
        defaultOperator = defaultOperator,
        df = df,
        docvalueFields = docvalueFields,
        expandWildcards = expandWildcards,
        explain = explain,
        from = from,
        ignoreThrottled = ignoreThrottled,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        lenient = lenient,
        maxConcurrentShardRequests = maxConcurrentShardRequests,
        preFilterShardSize = preFilterShardSize,
        preference = preference,
        q = q,
        requestCache = requestCache,
        restTotalHitsAsInt = restTotalHitsAsInt,
        routing = routing,
        scroll = scroll,
        searchType = searchType,
        seqNoPrimaryTerm = seqNoPrimaryTerm,
        size = size,
        sort = sort,
        source = source,
        sourceExcludes = sourceExcludes,
        sourceIncludes = sourceIncludes,
        stats = stats,
        storedFields = storedFields,
        suggestField = suggestField,
        suggestMode = suggestMode,
        suggestSize = suggestSize,
        suggestText = suggestText,
        terminateAfter = terminateAfter,
        timeout = timeout,
        trackScores = trackScores,
        trackTotalHits = trackTotalHits,
        typedKeys = typedKeys,
        version = version
      )
    )

  def search(request: SearchRequest): ZIO[ElasticSearchService, FrameworkException, SearchResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns information about the indices and shards that a search request would be executed against.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-shards.html
   *
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param routing Specific routing value
   */
  def searchShards(
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    local: Option[Boolean] = None,
    preference: Option[String] = None,
    routing: Option[String] = None
  ): ZIO[ElasticSearchService, FrameworkException, SearchShardsResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.searchShards(
        allowNoIndices = allowNoIndices,
        expandWildcards = expandWildcards,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        local = local,
        preference = preference,
        routing = routing
      )
    )

  def searchShards(
    request: SearchShardsRequest
  ): ZIO[ElasticSearchService, FrameworkException, SearchShardsResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Allows to use the Mustache language to pre-render a search definition.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-template.html
   *
   * @param body body the body of the call
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param ccsMinimizeRoundtrips Indicates whether network round-trips should be minimized as part of cross-cluster search requests execution
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param explain Specify whether to return detailed information about score computation as part of a hit
   * @param ignoreThrottled Whether specified concrete, expanded or aliased indices should be ignored when throttled
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param profile Specify whether to profile the query execution
   * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
   * @param routing A comma-separated list of specific routing values
   * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
   * @param searchType Search operation type
   * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
   */
  def searchTemplate(
    body: Json.Obj,
    allowNoIndices: Option[Boolean] = None,
    ccsMinimizeRoundtrips: Boolean = true,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    explain: Option[Boolean] = None,
    ignoreThrottled: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    preference: Option[String] = None,
    profile: Option[Boolean] = None,
    restTotalHitsAsInt: Boolean = false,
    routing: Seq[String] = Nil,
    scroll: Option[String] = None,
    searchType: Option[SearchType] = None,
    typedKeys: Option[Boolean] = None
  ): ZIO[ElasticSearchService, FrameworkException, SearchTemplateResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.searchTemplate(
        body = body,
        allowNoIndices = allowNoIndices,
        ccsMinimizeRoundtrips = ccsMinimizeRoundtrips,
        expandWildcards = expandWildcards,
        explain = explain,
        ignoreThrottled = ignoreThrottled,
        ignoreUnavailable = ignoreUnavailable,
        indices = indices,
        preference = preference,
        profile = profile,
        restTotalHitsAsInt = restTotalHitsAsInt,
        routing = routing,
        scroll = scroll,
        searchType = searchType,
        typedKeys = typedKeys
      )
    )

  def searchTemplate(
    request: SearchTemplateRequest
  ): ZIO[ElasticSearchService, FrameworkException, SearchTemplateResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Returns information and statistics about terms in the fields of a particular document.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-termvectors.html
   *
   * @param index The index in which the document resides.
   * @param id The id of the document, when not specified a doc param should be supplied.
   * @param body body the body of the call
   * @param fieldStatistics Specifies if document count, sum of document frequencies and sum of total term frequencies should be returned.
   * @param fields A comma-separated list of fields to return.
   * @param offsets Specifies if term offsets should be returned.
   * @param payloads Specifies if term payloads should be returned.
   * @param positions Specifies if term positions should be returned.
   * @param preference Specify the node or shard the operation should be performed on (default: random).
   * @param realtime Specifies if request is real-time as opposed to near-real-time (default: true).
   * @param routing Specific routing value.
   * @param termStatistics Specifies if total term frequency and document frequency should be returned.
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   */
  def termvectors(
    index: String,
    id: String,
    body: Option[Json.Obj] = None,
    fieldStatistics: Boolean = true,
    fields: Seq[String] = Nil,
    offsets: Boolean = true,
    payloads: Boolean = true,
    positions: Boolean = true,
    preference: Option[String] = None,
    realtime: Option[Boolean] = None,
    routing: Option[String] = None,
    termStatistics: Boolean = false,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None
  ): ZIO[ElasticSearchService, FrameworkException, TermVectorsResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.termvectors(
        index = index,
        id = id,
        body = body,
        fieldStatistics = fieldStatistics,
        fields = fields,
        offsets = offsets,
        payloads = payloads,
        positions = positions,
        preference = preference,
        realtime = realtime,
        routing = routing,
        termStatistics = termStatistics,
        version = version,
        versionType = versionType
      )
    )

  def termvectors(request: TermvectorsRequest): ZIO[ElasticSearchService, FrameworkException, TermVectorsResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Updates a document with a script or partial document.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-update.html
   *
   * @param index The name of the index
   * @param id Document ID
   * @param body body the body of the call
   * @param ifPrimaryTerm only perform the update operation if the last operation that has changed the document has the specified primary term
   * @param ifSeqNo only perform the update operation if the last operation that has changed the document has the specified sequence number
   * @param lang The script language (default: painless)
   * @param pipeline The pipeline id to preprocess incoming documents with
   * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param retryOnConflict Specify how many times should the operation be retried when a conflict occurs (default: 0)
   * @param routing Specific routing value
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param sourceExcludes A list of fields to exclude from the returned _source field
   * @param sourceIncludes A list of fields to extract and return from the _source field
   * @param timeout Explicit operation timeout
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the update operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def update(
    index: String,
    id: String,
    body: Json.Obj,
    ifPrimaryTerm: Option[Double] = None,
    ifSeqNo: Option[Double] = None,
    lang: Option[String] = None,
    pipeline: Option[String] = None,
    refresh: Option[Refresh] = None,
    retryOnConflict: Option[Double] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    timeout: Option[String] = None,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[ElasticSearchService, FrameworkException, UpdateResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.update(
        index = index,
        id = id,
        body = body,
        ifPrimaryTerm = ifPrimaryTerm,
        ifSeqNo = ifSeqNo,
        lang = lang,
        pipeline = pipeline,
        refresh = refresh,
        retryOnConflict = retryOnConflict,
        routing = routing,
        source = source,
        sourceExcludes = sourceExcludes,
        sourceIncludes = sourceIncludes,
        timeout = timeout,
        version = version,
        versionType = versionType,
        waitForActiveShards = waitForActiveShards
      )
    )

  def update(request: UpdateRequest): ZIO[ElasticSearchService, FrameworkException, UpdateResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Performs an update on every document in the index without changing the source,
for example to pick up a mapping change.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-update-by-query.html
   *
   * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
   * @param analyzer The analyzer to use for the query string
   * @param body body the body of the call
   * @param conflicts What to do when the update by query hits version conflicts?
   * @param defaultOperator The default operator for query string query (AND or OR)
   * @param df The field to use as default where no field prefix is given in the query string
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param from Starting offset (default: 0)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param maxDocs Maximum number of documents to process (default: all documents)
   * @param pipeline Ingest pipeline to set on index requests made by this action. (default: none)
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param q Query in the Lucene query string syntax
   * @param refresh Should the effected indexes be refreshed?
   * @param requestCache Specify if request cache should be used for this request or not, defaults to index level setting
   * @param requestsPerSecond The throttle to set on this request in sub-requests per second. -1 means no throttle.
   * @param routing A comma-separated list of specific routing values
   * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
   * @param scrollSize Size on the scroll request powering the update by query
   * @param searchTimeout Explicit timeout for each search request. Defaults to no timeout.
   * @param searchType Search operation type
   * @param slices The number of slices this task should be divided into. Defaults to 1 meaning the task isn't sliced into subtasks.
   * @param sort A comma-separated list of <field>:<direction> pairs
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param sourceExcludes A list of fields to exclude from the returned _source field
   * @param sourceIncludes A list of fields to extract and return from the _source field
   * @param stats Specific 'tag' of the request for logging and statistical purposes
   * @param terminateAfter The maximum number of documents to collect for each shard, upon reaching which the query execution will terminate early.
   * @param timeout Time each individual bulk request should wait for shards that are unavailable.
   * @param version Specify whether to return document version as part of a hit
   * @param versionType Should the document increment the version number (internal) on hit or not (reindex)
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the update by query operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   * @param waitForCompletion Should the request should block until the update by query operation is complete.
   */
  def updateByQuery(
    body: Json.Obj,
    indices: Seq[String] = Nil,
    allowNoIndices: Option[Boolean] = None,
    analyzeWildcard: Option[Boolean] = None,
    analyzer: Option[String] = None,
    conflicts: Seq[Conflicts] = Nil,
    defaultOperator: DefaultOperator = DefaultOperator.OR,
    df: Option[String] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    from: Option[Int] = None,
    ignoreUnavailable: Option[Boolean] = None,
    lenient: Option[Boolean] = None,
    maxDocs: Option[Double] = None,
    pipeline: Option[String] = None,
    preference: Option[String] = None,
    q: Option[String] = None,
    refresh: Option[Boolean] = None,
    requestCache: Option[Boolean] = None,
    requestsPerSecond: Int = 0,
    routing: Seq[String] = Nil,
    scroll: Option[String] = None,
    scrollSize: Option[Double] = None,
    searchTimeout: Option[String] = None,
    searchType: Option[SearchType] = None,
    slices: Option[Int] = None,
    sort: Seq[String] = Nil,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    stats: Seq[String] = Nil,
    terminateAfter: Option[Long] = None,
    timeout: String = "1m",
    version: Option[Boolean] = None,
    versionType: Option[Boolean] = None,
    waitForActiveShards: Option[String] = None,
    waitForCompletion: Boolean = true
  ): ZIO[ElasticSearchService, FrameworkException, ActionByQueryResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.updateByQuery(
        indices = indices,
        allowNoIndices = allowNoIndices,
        analyzeWildcard = analyzeWildcard,
        analyzer = analyzer,
        body = body,
        conflicts = conflicts,
        defaultOperator = defaultOperator,
        df = df,
        expandWildcards = expandWildcards,
        from = from,
        ignoreUnavailable = ignoreUnavailable,
        lenient = lenient,
        maxDocs = maxDocs,
        pipeline = pipeline,
        preference = preference,
        q = q,
        refresh = refresh,
        requestCache = requestCache,
        requestsPerSecond = requestsPerSecond,
        routing = routing,
        scroll = scroll,
        scrollSize = scrollSize,
        searchTimeout = searchTimeout,
        searchType = searchType,
        slices = slices,
        sort = sort,
        source = source,
        sourceExcludes = sourceExcludes,
        sourceIncludes = sourceIncludes,
        stats = stats,
        terminateAfter = terminateAfter,
        timeout = timeout,
        version = version,
        versionType = versionType,
        waitForActiveShards = waitForActiveShards,
        waitForCompletion = waitForCompletion
      )
    )

  def updateByQuery(
    request: UpdateByQueryRequest
  ): ZIO[ElasticSearchService, FrameworkException, ActionByQueryResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

  /*
   * Changes the number of requests per second for a particular Update By Query operation.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update-by-query.html
   *
   * @param requestsPerSecond The throttle to set on this request in floating sub-requests per second. -1 means set no throttle.
   * @param taskId The task id to rethrottle
   */
  def updateByQueryRethrottle(
    requestsPerSecond: Int,
    taskId: String
  ): ZIO[ElasticSearchService, FrameworkException, UpdateByQueryRethrottleResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](
      _.get.updateByQueryRethrottle(requestsPerSecond = requestsPerSecond, taskId = taskId)
    )

  def updateByQueryRethrottle(
    request: UpdateByQueryRethrottleRequest
  ): ZIO[ElasticSearchService, FrameworkException, UpdateByQueryRethrottleResponse] =
    ZIO.environmentWithZIO[ElasticSearchService](_.get.execute(request))

}
