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

package zio.elasticsearch.common

import zio.{ ZIO, _ }
import zio.elasticsearch._
import zio.elasticsearch.common.bulk.{ BulkRequest, BulkResponse }
import zio.elasticsearch.common.clear_scroll.{ ClearScrollRequest, ClearScrollResponse }
import zio.elasticsearch.common.close_point_in_time.{ ClosePointInTimeRequest, ClosePointInTimeResponse }
import zio.elasticsearch.common.count.{ CountRequest, CountResponse }
import zio.elasticsearch.common.create.{ CreateRequest, CreateResponse }
import zio.elasticsearch.common.delete.{ DeleteRequest, DeleteResponse }
import zio.elasticsearch.common.delete_by_query.{ DeleteByQueryRequest, DeleteByQueryResponse }
import zio.elasticsearch.common.delete_by_query_rethrottle.{
  DeleteByQueryRethrottleRequest,
  DeleteByQueryRethrottleResponse
}
import zio.elasticsearch.common.delete_script.{ DeleteScriptRequest, DeleteScriptResponse }
import zio.elasticsearch.common.exists.{ ExistsRequest, ExistsResponse }
import zio.elasticsearch.common.exists_source.{ ExistsSourceRequest, ExistsSourceResponse }
import zio.elasticsearch.common.explain.{ ExplainRequest, ExplainResponse }
import zio.elasticsearch.common.field_caps.{ FieldCapsRequest, FieldCapsResponse }
import zio.elasticsearch.common.get.{ GetRequest, GetResponse }
import zio.elasticsearch.common.get_script.{ GetScriptRequest, GetScriptResponse }
import zio.elasticsearch.common.get_script_context.{ GetScriptContextRequest, GetScriptContextResponse }
import zio.elasticsearch.common.get_script_languages.{ GetScriptLanguagesRequest, GetScriptLanguagesResponse }
import zio.elasticsearch.common.get_source.{ GetSourceRequest, GetSourceResponse }
import zio.elasticsearch.common.index.{ IndexRequest, IndexResponse }
import zio.elasticsearch.common.info.{ InfoRequest, InfoResponse }
import zio.elasticsearch.common.knn_search.{ KnnSearchRequest, KnnSearchResponse }
import zio.elasticsearch.common.mget.{ MultiGetRequest, MultiGetResponse }
import zio.elasticsearch.common.msearch.{ MultiSearchRequest, MultiSearchResponse }
import zio.elasticsearch.common.msearch_template.{ MsearchTemplateRequest, MsearchTemplateResponse }
import zio.elasticsearch.common.mtermvectors.{ MultiTermVectorsRequest, MultiTermVectorsResponse }
import zio.elasticsearch.common.open_point_in_time.{ OpenPointInTimeRequest, OpenPointInTimeResponse }
import zio.elasticsearch.common.ping.{ PingRequest, PingResponse }
import zio.elasticsearch.common.put_script.{ PutScriptRequest, PutScriptResponse }
import zio.elasticsearch.common.rank_eval.{ RankEvalRequest, RankEvalResponse }
import zio.elasticsearch.common.reindex.{ ReindexRequest, ReindexResponse }
import zio.elasticsearch.common.reindex_rethrottle.{ ReindexRethrottleRequest, ReindexRethrottleResponse }
import zio.elasticsearch.common.render_search_template.{ RenderSearchTemplateRequest, RenderSearchTemplateResponse }
import zio.elasticsearch.common.requests._
import zio.elasticsearch.common.scripts_painless_execute.{
  ScriptsPainlessExecuteRequest,
  ScriptsPainlessExecuteResponse
}
import zio.elasticsearch.common.scroll.{ ScrollRequest, ScrollResponse }
import zio.elasticsearch.common.search.{ SearchRequest, SearchResponse, SearchType }
import zio.elasticsearch.common.search_mvt.{ SearchMvtRequest, SearchMvtResponse }
import zio.elasticsearch.common.search_shards.{ SearchShardsRequest, SearchShardsResponse }
import zio.elasticsearch.common.search_template.{ SearchTemplateRequest, SearchTemplateResponse }
import zio.elasticsearch.common.semantic_search.{ SemanticSearchRequest, SemanticSearchResponse }
import zio.elasticsearch.common.terms_enum.{ TermsEnumRequest, TermsEnumResponse }
import zio.elasticsearch.common.termvectors.{ TermvectorsRequest, TermvectorsResponse }
import zio.elasticsearch.common.update.{ UpdateRequest, UpdateResponse }
import zio.elasticsearch.common.update_by_query.{ UpdateByQueryRequest, UpdateByQueryResponse }
import zio.elasticsearch.common.update_by_query_rethrottle.{
  UpdateByQueryRethrottleRequest,
  UpdateByQueryRethrottleResponse
}
import zio.exception._
import zio.json.ast._

trait CommonManager {
  def httpService: ElasticSearchHttpService

  /* start special methods */
  def addToBulk(action: IndexRequest): ZIO[Any, FrameworkException, IndexResponse]

  /* end special methods */

  /*
   * Allows to perform multiple index/update/delete operations in a single request.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-bulk.html
   *
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param index Default index for items which don't provide one
   * @param pipeline The pipeline id to preprocess incoming documents with
   * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param requireAlias Sets require_alias for all incoming documents. Defaults to unset (false)
   * @param routing Specific routing value
   * @param source True or false to return the _source field or not, or default list of fields to return, can be overridden on each sub-request
   * @param sourceExcludes Default list of fields to exclude from the returned _source field, can be overridden on each sub-request
   * @param sourceIncludes Default list of fields to extract and return from the _source field, can be overridden on each sub-request
   * @param timeout Explicit operation timeout
   * @param `type` Default document type for items which don't provide one
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the bulk operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def bulk(
    body: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    index: Option[String] = None,
    pipeline: Option[String] = None,
    refresh: Option[Refresh] = None,
    requireAlias: Option[Boolean] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    timeout: Option[String] = None,
    `type`: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[Any, FrameworkException, BulkResponse] = {
    val request = BulkRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      index = index,
      pipeline = pipeline,
      refresh = refresh,
      requireAlias = requireAlias,
      routing = routing,
      source = source,
      sourceExcludes = sourceExcludes,
      sourceIncludes = sourceIncludes,
      timeout = timeout,
      `type` = `type`,
      waitForActiveShards = waitForActiveShards
    )

    bulk(request)

  }

  def bulk(request: BulkRequest): ZIO[Any, FrameworkException, BulkResponse] =
    httpService.execute[Chunk[String], BulkResponse](request)

  /*
   * Explicitly clears the search context for a scroll.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/clear-scroll-api.html
   *
   * @param scrollId

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   */
  def clearScroll(
    body: ClearScrollRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ClearScrollResponse] = {
    val request =
      ClearScrollRequest(errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty, body = body)

    clearScroll(request)

  }

  def clearScroll(request: ClearScrollRequest): ZIO[Any, FrameworkException, ClearScrollResponse] =
    httpService.execute[ClearScrollRequestBody, ClearScrollResponse](request)

  /*
   * Close a point in time
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/point-in-time-api.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   */
  def closePointInTime(
    body: ClosePointInTimeRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ClosePointInTimeResponse] = {
    val request = ClosePointInTimeRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body
    )

    closePointInTime(request)

  }

  def closePointInTime(request: ClosePointInTimeRequest): ZIO[Any, FrameworkException, ClosePointInTimeResponse] =
    httpService.execute[ClosePointInTimeRequestBody, ClosePointInTimeResponse](request)

  /*
   * Returns number of documents matching a query.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-count.html
   *
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
    index: Chunk[String],
    body: CountRequestBody = CountRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    analyzeWildcard: Option[Boolean] = None,
    analyzer: Option[String] = None,
    defaultOperator: DefaultOperator = DefaultOperator.OR,
    df: Option[String] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreThrottled: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    lenient: Option[Boolean] = None,
    minScore: Option[Double] = None,
    preference: Option[String] = None,
    q: Option[String] = None,
    routing: Seq[String] = Nil,
    terminateAfter: Option[Long] = None
  ): ZIO[Any, FrameworkException, CountResponse] = {
    val request = CountRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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

    count(request)

  }

  def count(request: CountRequest): ZIO[Any, FrameworkException, CountResponse] =
    httpService.execute[CountRequestBody, CountResponse](request)

  /*
   * Creates a new document in the index.

Returns a 409 response when a document with a same ID already exists in the index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-index_.html
   *
   * @param index The name of the index
   * @param id Document ID
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
    body: TDocument,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    pipeline: Option[String] = None,
    refresh: Option[Refresh] = None,
    routing: Option[String] = None,
    timeout: Option[String] = None,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[Any, FrameworkException, CreateResponse] = {
    val request = CreateRequest(
      index = index,
      id = id,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      pipeline = pipeline,
      refresh = refresh,
      routing = routing,
      timeout = timeout,
      version = version,
      versionType = versionType,
      waitForActiveShards = waitForActiveShards
    )

    create(request)

  }

  def create(request: CreateRequest): ZIO[Any, FrameworkException, CreateResponse] =
    httpService.execute[TDocument, CreateResponse](request)

  /*
   * Removes a document from the index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-delete.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param ifPrimaryTerm only perform the delete operation if the last operation that has changed the document has the specified primary term
   * @param ifSeqNo only perform the delete operation if the last operation that has changed the document has the specified sequence number
   * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param routing Specific routing value
   * @param timeout Explicit operation timeout
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the delete operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def delete(
    index: String,
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    ifPrimaryTerm: Option[Double] = None,
    ifSeqNo: Option[Double] = None,
    refresh: Option[Refresh] = None,
    routing: Option[String] = None,
    timeout: Option[String] = None,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteResponse] = {
    val request = DeleteRequest(
      index = index,
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      ifPrimaryTerm = ifPrimaryTerm,
      ifSeqNo = ifSeqNo,
      refresh = refresh,
      routing = routing,
      timeout = timeout,
      version = version,
      versionType = versionType,
      waitForActiveShards = waitForActiveShards
    )

    delete(request)

  }

  def delete(request: DeleteRequest): ZIO[Any, FrameworkException, DeleteResponse] =
    httpService.execute[Json, DeleteResponse](request)

  /*
   * Deletes documents matching the provided query.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-delete-by-query.html
   *
   * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param body body the body of the call
   * @param taskId

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
   * @param refresh Should the affected indexes be refreshed?
   * @param requestCache Specify if request cache should be used for this request or not, defaults to index level setting
   * @param requestsPerSecond The throttle for this request in sub-requests per second. -1 means no throttle.
   * @param routing A comma-separated list of specific routing values
   * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
   * @param scrollSize Size on the scroll request powering the delete by query
   * @param searchTimeout Explicit timeout for each search request. Defaults to no timeout.
   * @param searchType Search operation type
   * @param slices The number of slices this task should be divided into. Defaults to 1, meaning the task isn't sliced into subtasks. Can be set to `auto`.
   * @param sort A comma-separated list of <field>:<direction> pairs
   * @param stats Specific 'tag' of the request for logging and statistical purposes
   * @param terminateAfter The maximum number of documents to collect for each shard, upon reaching which the query execution will terminate early.
   * @param timeout Time each individual bulk request should wait for shards that are unavailable.
   * @param version Specify whether to return document version as part of a hit
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the delete by query operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   * @param waitForCompletion Should the request should block until the delete by query is complete.
   */
  def deleteByQuery(
    indices: Seq[String] = Nil,
    body: Json,
    taskId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
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
    requestsPerSecond: Double = 0,
    routing: Seq[String] = Nil,
    scroll: Option[String] = None,
    scrollSize: Double = 100,
    searchTimeout: Option[String] = None,
    searchType: Option[SearchType] = None,
    slices: String = "1",
    sort: Seq[String] = Nil,
    stats: Seq[String] = Nil,
    terminateAfter: Option[Long] = None,
    timeout: String = "1m",
    version: Option[Boolean] = None,
    waitForActiveShards: Option[String] = None,
    waitForCompletion: Boolean = true
  ): ZIO[Any, FrameworkException, DeleteByQueryResponse] = {
    val request = DeleteByQueryRequest(
      indices = indices,
      body = body,
      taskId = taskId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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
      stats = stats,
      terminateAfter = terminateAfter,
      timeout = timeout,
      version = version,
      waitForActiveShards = waitForActiveShards,
      waitForCompletion = waitForCompletion
    )

    deleteByQuery(request)

  }

  def deleteByQuery(request: DeleteByQueryRequest): ZIO[Any, FrameworkException, DeleteByQueryResponse] =
    httpService.execute[Json, DeleteByQueryResponse](request)

  /*
   * Changes the number of requests per second for a particular Delete By Query operation.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete-by-query.html
   *
   * @param requestsPerSecond The throttle to set on this request in floating sub-requests per second. -1 means set no throttle.
   * @param taskId The task id to rethrottle
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def deleteByQueryRethrottle(
    requestsPerSecond: Integer,
    taskId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, DeleteByQueryRethrottleResponse] = {
    val request = DeleteByQueryRethrottleRequest(
      requestsPerSecond = requestsPerSecond,
      taskId = taskId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deleteByQueryRethrottle(request)

  }

  def deleteByQueryRethrottle(
    request: DeleteByQueryRethrottleRequest
  ): ZIO[Any, FrameworkException, DeleteByQueryRethrottleResponse] =
    httpService.execute[Json, DeleteByQueryRethrottleResponse](request)

  /*
   * Deletes a script.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
   *
   * @param id Script ID
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def deleteScript(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteScriptResponse] = {
    val request = DeleteScriptRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    deleteScript(request)

  }

  def deleteScript(request: DeleteScriptRequest): ZIO[Any, FrameworkException, DeleteScriptResponse] =
    httpService.execute[Json, DeleteScriptResponse](request)

  /*
   * Returns information about whether a document exists in an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
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
  ): ZIO[Any, FrameworkException, ExistsResponse] = {
    val request = ExistsRequest(
      index = index,
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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

    exists(request)

  }

  def exists(request: ExistsRequest): ZIO[Any, FrameworkException, ExistsResponse] =
    httpService.execute[Json, ExistsResponse](request)

  /*
   * Returns information about whether a document source exists in an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    preference: Option[String] = None,
    realtime: Option[Boolean] = None,
    refresh: Option[Boolean] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None
  ): ZIO[Any, FrameworkException, ExistsSourceResponse] = {
    val request = ExistsSourceRequest(
      index = index,
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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

    existsSource(request)

  }

  def existsSource(request: ExistsSourceRequest): ZIO[Any, FrameworkException, ExistsSourceResponse] =
    httpService.execute[Json, ExistsSourceResponse](request)

  /*
   * Returns information about why a specific matches (or doesn't match) a query.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-explain.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
    body: ExplainRequestBody = ExplainRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
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
  ): ZIO[Any, FrameworkException, ExplainResponse] = {
    val request = ExplainRequest(
      index = index,
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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

    explain(request)

  }

  def explain(request: ExplainRequest): ZIO[Any, FrameworkException, ExplainResponse] =
    httpService.execute[ExplainRequestBody, ExplainResponse](request)

  /*
   * Returns the information about the capabilities of fields among multiple indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-field-caps.html
   *
   * @param index Comma-separated list of data streams, indices, and aliases used to limit the request. Supports wildcards (*). To target all data streams and indices, omit this parameter or use * or _all.

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param body body the body of the call
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param fields A comma-separated list of field names
   * @param filters An optional set of filters: can include +metadata,-metadata,-nested,-multifield,-parent
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param includeUnmapped Indicates whether unmapped fields should be included in the response.
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param types Only return results for fields that have one of the types in the list
   */
  def fieldCaps(
    body: FieldCapsRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    fields: Seq[String] = Nil,
    filters: Seq[String] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    includeUnmapped: Boolean = false,
    indices: Seq[String] = Nil,
    types: Seq[String] = Nil
  ): ZIO[Any, FrameworkException, FieldCapsResponse] = {
    val request = FieldCapsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      body = body,
      expandWildcards = expandWildcards,
      fields = fields,
      filters = filters,
      ignoreUnavailable = ignoreUnavailable,
      includeUnmapped = includeUnmapped,
      indices = indices,
      types = types
    )

    fieldCaps(request)

  }

  def fieldCaps(request: FieldCapsRequest): ZIO[Any, FrameworkException, FieldCapsResponse] =
    httpService.execute[FieldCapsRequestBody, FieldCapsResponse](request)

  /*
   * Returns a document.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param forceSyntheticSource Should this request force synthetic _source? Use this to test if the mapping supports synthetic _source and to get a sense of the worst case performance. Fetches with this enabled will be slower the enabling synthetic source natively in the index.
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
  def get(
    index: String,
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    forceSyntheticSource: Option[Boolean] = None,
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
  ): ZIO[Any, FrameworkException, GetResponse] = {
    val request = GetRequest(
      index = index,
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      forceSyntheticSource = forceSyntheticSource,
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

    get(request)

  }

  def get(request: GetRequest): ZIO[Any, FrameworkException, GetResponse] =
    httpService.execute[Json, GetResponse](request)

  /*
   * Returns a script.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
   *
   * @param id Script ID
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param masterTimeout Specify timeout for connection to master
   */
  def getScript(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, GetScriptResponse] = {
    val request = GetScriptRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout
    )

    getScript(request)

  }

  def getScript(request: GetScriptRequest): ZIO[Any, FrameworkException, GetScriptResponse] =
    httpService.execute[Json, GetScriptResponse](request)

  /*
   * Returns all script contexts.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/painless/master/painless-contexts.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def getScriptContext(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetScriptContextResponse] = {
    val request =
      GetScriptContextRequest(errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    getScriptContext(request)

  }

  def getScriptContext(request: GetScriptContextRequest): ZIO[Any, FrameworkException, GetScriptContextResponse] =
    httpService.execute[Json, GetScriptContextResponse](request)

  /*
   * Returns available script types, languages and contexts
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def getScriptLanguages(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetScriptLanguagesResponse] = {
    val request =
      GetScriptLanguagesRequest(errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    getScriptLanguages(request)

  }

  def getScriptLanguages(request: GetScriptLanguagesRequest): ZIO[Any, FrameworkException, GetScriptLanguagesResponse] =
    httpService.execute[Json, GetScriptLanguagesResponse](request)

  /*
   * Returns the source of a document.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
   *
   * @param index The name of the index
   * @param id The document ID
   * @param storedFields

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
    storedFields: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    preference: Option[String] = None,
    realtime: Option[Boolean] = None,
    refresh: Option[Boolean] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None
  ): ZIO[Any, FrameworkException, GetSourceResponse] = {
    val request = GetSourceRequest(
      index = index,
      id = id,
      storedFields = storedFields,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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

    getSource(request)

  }

  def getSource(request: GetSourceRequest): ZIO[Any, FrameworkException, GetSourceResponse] =
    httpService.execute[Json, GetSourceResponse](request)

  /*
   * Creates or updates a document in an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-index_.html
   *
   * @param index The name of the index
   * @param id Document ID
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param ifPrimaryTerm only perform the index operation if the last operation that has changed the document has the specified primary term
   * @param ifSeqNo only perform the index operation if the last operation that has changed the document has the specified sequence number
   * @param opType Explicit operation type. Defaults to `index` for requests with an explicit document ID, and to `create`for requests without an explicit document ID
   * @param pipeline The pipeline id to preprocess incoming documents with
   * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param requireAlias When true, requires destination to be an alias. Default is false
   * @param routing Specific routing value
   * @param timeout Explicit operation timeout
   * @param version Explicit version number for concurrency control
   * @param versionType Specific version type
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the index operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def index(
    index: String,
    body: TDocument,
    id: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    ifPrimaryTerm: Option[Double] = None,
    ifSeqNo: Option[Double] = None,
    opType: OpType = OpType.index,
    pipeline: Option[String] = None,
    refresh: Option[Refresh] = None,
    requireAlias: Option[Boolean] = None,
    routing: Option[String] = None,
    timeout: Option[String] = None,
    version: Option[Long] = None,
    versionType: Option[VersionType] = None,
    waitForActiveShards: Option[String] = None,
    bulk: Boolean = false
  ): ZIO[Any, FrameworkException, IndexResponse] = {
    val request = IndexRequest(
      index = index,
      id = id,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      ifPrimaryTerm = ifPrimaryTerm,
      ifSeqNo = ifSeqNo,
      opType = opType,
      pipeline = pipeline,
      refresh = refresh,
      requireAlias = requireAlias,
      routing = routing,
      timeout = timeout,
      version = version,
      versionType = versionType,
      waitForActiveShards = waitForActiveShards
    )

    def applyReqOrBulk(request: IndexRequest, bulk: Boolean): ZioResponse[IndexResponse] =
      if (bulk) {
        this.addToBulk(request) *>
          ZIO.succeed(
            IndexResponse(
              index = request.index,
              id = request.id.getOrElse(""),
              version = 0
            )
          )

      } else
        this.index(request)

    applyReqOrBulk(request, bulk)

  }

  def index(request: IndexRequest): ZIO[Any, FrameworkException, IndexResponse] =
    httpService.execute[TDocument, IndexResponse](request)

  /*
   * Returns basic information about the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def info(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, InfoResponse] = {
    val request = InfoRequest(errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    info(request)

  }

  def info(request: InfoRequest): ZIO[Any, FrameworkException, InfoResponse] =
    httpService.execute[Json, InfoResponse](request)

  /*
   * Performs a kNN search.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-search.html
   *
   * @param indices A comma-separated list of index names to search; use `_all` to perform the operation on all indices
   * @param index A comma-separated list of index names to search;
   * use `_all` or to perform the operation on all indices

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param routing A comma-separated list of specific routing values
   */
  def knnSearch(
    indices: Seq[String] = Nil,
    body: KnnSearchRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    routing: Seq[String] = Nil
  ): ZIO[Any, FrameworkException, KnnSearchResponse] = {
    val request = KnnSearchRequest(
      indices = indices,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      routing = routing
    )

    knnSearch(request)

  }

  def knnSearch(request: KnnSearchRequest): ZIO[Any, FrameworkException, KnnSearchResponse] =
    httpService.execute[KnnSearchRequestBody, KnnSearchResponse](request)

  /*
   * Allows to get multiple documents in one request.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-multi-get.html
   *
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param forceSyntheticSource Should this request force synthetic _source? Use this to test if the mapping supports synthetic _source and to get a sense of the worst case performance. Fetches with this enabled will be slower the enabling synthetic source natively in the index.
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
    body: MultiGetRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    forceSyntheticSource: Option[Boolean] = None,
    index: Option[String] = None,
    preference: Option[String] = None,
    realtime: Option[Boolean] = None,
    refresh: Option[Boolean] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    storedFields: Seq[String] = Nil
  ): ZIO[Any, FrameworkException, MultiGetResponse] = {
    val request = MultiGetRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      forceSyntheticSource = forceSyntheticSource,
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

    mget(request)

  }

  def mget(request: MultiGetRequest): ZIO[Any, FrameworkException, MultiGetResponse] =
    httpService.execute[MultiGetRequestBody, MultiGetResponse](request)

  /*
   * Allows to execute several search operations in one request.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-multi-search.html
   *
   * @param index

   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param ccsMinimizeRoundtrips Indicates whether network round-trips should be minimized as part of cross-cluster search requests execution
   * @param indices A comma-separated list of index names to use as default
   * @param maxConcurrentSearches Controls the maximum number of concurrent searches the multi search api will execute
   * @param maxConcurrentShardRequests The number of concurrent shard requests each sub search executes concurrently per node. This value should be used to limit the impact of the search on the cluster in order to limit the number of concurrent shard requests
   * @param preFilterShardSize A threshold that enforces a pre-filter roundtrip to prefilter search shards based on query rewriting if the number of shards the search request expands to exceeds the threshold. This filter roundtrip can limit the number of shards significantly if for instance a shard can not match any documents based on its rewrite method ie. if date filters are mandatory to match but the shard bounds and the query are disjoint.
   * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
   * @param searchType Search operation type
   * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
   */
  def msearch(
    index: Chunk[String],
    body: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    ccsMinimizeRoundtrips: Boolean = true,
    indices: Seq[String] = Nil,
    maxConcurrentSearches: Option[Double] = None,
    maxConcurrentShardRequests: Double = 5,
    preFilterShardSize: Option[Double] = None,
    restTotalHitsAsInt: Boolean = false,
    searchType: Option[SearchType] = None,
    typedKeys: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, MultiSearchResponse] = {
    val request = MultiSearchRequest(
      index = index,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      ccsMinimizeRoundtrips = ccsMinimizeRoundtrips,
      indices = indices,
      maxConcurrentSearches = maxConcurrentSearches,
      maxConcurrentShardRequests = maxConcurrentShardRequests,
      preFilterShardSize = preFilterShardSize,
      restTotalHitsAsInt = restTotalHitsAsInt,
      searchType = searchType,
      typedKeys = typedKeys
    )

    msearch(request)

  }

  def msearch(request: MultiSearchRequest): ZIO[Any, FrameworkException, MultiSearchResponse] =
    httpService.execute[Chunk[String], MultiSearchResponse](request)

  /*
   * Allows to execute several search template operations in one request.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-multi-search.html
   *
   * @param index

   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param ccsMinimizeRoundtrips Indicates whether network round-trips should be minimized as part of cross-cluster search requests execution
   * @param indices A comma-separated list of index names to use as default
   * @param maxConcurrentSearches Controls the maximum number of concurrent searches the multi search api will execute
   * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
   * @param searchType Search operation type
   * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
   */
  def msearchTemplate(
    index: Chunk[String],
    body: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    ccsMinimizeRoundtrips: Boolean = true,
    indices: Seq[String] = Nil,
    maxConcurrentSearches: Option[Double] = None,
    restTotalHitsAsInt: Boolean = false,
    searchType: Option[SearchType] = None,
    typedKeys: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, MsearchTemplateResponse] = {
    val request = MsearchTemplateRequest(
      index = index,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      ccsMinimizeRoundtrips = ccsMinimizeRoundtrips,
      indices = indices,
      maxConcurrentSearches = maxConcurrentSearches,
      restTotalHitsAsInt = restTotalHitsAsInt,
      searchType = searchType,
      typedKeys = typedKeys
    )

    msearchTemplate(request)

  }

  def msearchTemplate(request: MsearchTemplateRequest): ZIO[Any, FrameworkException, MsearchTemplateResponse] =
    httpService.execute[Chunk[String], MsearchTemplateResponse](request)

  /*
   * Returns multiple termvectors in one request.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-multi-termvectors.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
    body: MultiTermVectorsRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
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
  ): ZIO[Any, FrameworkException, MultiTermVectorsResponse] = {
    val request = MultiTermVectorsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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

    mtermvectors(request)

  }

  def mtermvectors(request: MultiTermVectorsRequest): ZIO[Any, FrameworkException, MultiTermVectorsResponse] =
    httpService.execute[MultiTermVectorsRequestBody, MultiTermVectorsResponse](request)

  /*
   * Open a point in time that can be used in subsequent searches
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/point-in-time-api.html
   *
   * @param keepAlive Specific the time to live for the point in time
   * @param indices A comma-separated list of index names to open point in time; use `_all` or empty string to perform the operation on all indices
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param routing Specific routing value
   */
  def openPointInTime(
    keepAlive: String,
    indices: Seq[String] = Nil,
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    preference: Option[String] = None,
    routing: Option[String] = None
  ): ZIO[Any, FrameworkException, OpenPointInTimeResponse] = {
    val request = OpenPointInTimeRequest(
      keepAlive = keepAlive,
      indices = indices,
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      preference = preference,
      routing = routing
    )

    openPointInTime(request)

  }

  def openPointInTime(request: OpenPointInTimeRequest): ZIO[Any, FrameworkException, OpenPointInTimeResponse] =
    httpService.execute[Json, OpenPointInTimeResponse](request)

  /*
   * Returns whether the cluster is running.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def ping(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, PingResponse] = {
    val request = PingRequest(errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    ping(request)

  }

  def ping(request: PingRequest): ZIO[Any, FrameworkException, PingResponse] =
    httpService.execute[Json, PingResponse](request)

  /*
   * Creates or updates a script.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
   *
   * @param id Script ID
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param context Context name to compile script against
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def putScript(
    id: String,
    body: PutScriptRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    context: Option[String] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, PutScriptResponse] = {
    val request = PutScriptRequest(
      id = id,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      context = context,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    putScript(request)

  }

  def putScript(request: PutScriptRequest): ZIO[Any, FrameworkException, PutScriptResponse] =
    httpService.execute[PutScriptRequestBody, PutScriptResponse](request)

  /*
   * Allows to evaluate the quality of ranked search results over a set of typical search queries
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-rank-eval.html
   *
   * @param index Comma-separated list of data streams, indices, and index aliases used to limit the request. Wildcard (`*`) expressions are supported.
   * To target all data streams and indices in a cluster, omit this parameter or use `_all` or `*`.

   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param searchType Search operation type
   */
  def rankEval(
    index: Chunk[String],
    body: RankEvalRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    searchType: Option[SearchType] = None
  ): ZIO[Any, FrameworkException, RankEvalResponse] = {
    val request = RankEvalRequest(
      index = index,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      indices = indices,
      searchType = searchType
    )

    rankEval(request)

  }

  def rankEval(request: RankEvalRequest): ZIO[Any, FrameworkException, RankEvalResponse] =
    httpService.execute[RankEvalRequestBody, RankEvalResponse](request)

  /*
   * Allows to copy documents from one index to another, optionally filtering the source
documents by a query, changing the destination index settings, or fetching the
documents from a remote cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-reindex.html
   *
   * @param requireAlias

   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param maxDocs Maximum number of documents to process (default: all documents)
   * @param refresh Should the affected indexes be refreshed?
   * @param requestsPerSecond The throttle to set on this request in sub-requests per second. -1 means no throttle.
   * @param scroll Control how long to keep the search context alive
   * @param slices The number of slices this task should be divided into. Defaults to 1, meaning the task isn't sliced into subtasks. Can be set to `auto`.
   * @param timeout Time each individual bulk request should wait for shards that are unavailable.
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the reindex operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   * @param waitForCompletion Should the request should block until the reindex is complete.
   */
  def reindex(
    requireAlias: Boolean,
    body: ReindexRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    maxDocs: Option[Double] = None,
    refresh: Option[Boolean] = None,
    requestsPerSecond: Double = 0,
    scroll: String = "5m",
    slices: String = "1",
    timeout: String = "1m",
    waitForActiveShards: Option[String] = None,
    waitForCompletion: Boolean = true
  ): ZIO[Any, FrameworkException, ReindexResponse] = {
    val request = ReindexRequest(
      requireAlias = requireAlias,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      maxDocs = maxDocs,
      refresh = refresh,
      requestsPerSecond = requestsPerSecond,
      scroll = scroll,
      slices = slices,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards,
      waitForCompletion = waitForCompletion
    )

    reindex(request)

  }

  def reindex(request: ReindexRequest): ZIO[Any, FrameworkException, ReindexResponse] =
    httpService.execute[ReindexRequestBody, ReindexResponse](request)

  /*
   * Changes the number of requests per second for a particular Reindex operation.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-reindex.html
   *
   * @param requestsPerSecond The throttle to set on this request in floating sub-requests per second. -1 means set no throttle.
   * @param taskId The task id to rethrottle
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def reindexRethrottle(
    requestsPerSecond: Double,
    taskId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ReindexRethrottleResponse] = {
    val request = ReindexRethrottleRequest(
      requestsPerSecond = requestsPerSecond,
      taskId = taskId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    reindexRethrottle(request)

  }

  def reindexRethrottle(request: ReindexRethrottleRequest): ZIO[Any, FrameworkException, ReindexRethrottleResponse] =
    httpService.execute[Json, ReindexRethrottleResponse](request)

  /*
   * Allows to use the Mustache language to pre-render a search definition.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/render-search-template-api.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param id The id of the stored search template
   */
  def renderSearchTemplate(
    body: RenderSearchTemplateRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    id: Option[String] = None
  ): ZIO[Any, FrameworkException, RenderSearchTemplateResponse] = {
    val request = RenderSearchTemplateRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      id = id
    )

    renderSearchTemplate(request)

  }

  def renderSearchTemplate(
    request: RenderSearchTemplateRequest
  ): ZIO[Any, FrameworkException, RenderSearchTemplateResponse] =
    httpService.execute[RenderSearchTemplateRequestBody, RenderSearchTemplateResponse](request)

  /*
   * Allows an arbitrary script to be executed and a result to be returned
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/painless/master/painless-execute-api.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   */
  def scriptsPainlessExecute(
    body: ScriptsPainlessExecuteRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ScriptsPainlessExecuteResponse] = {
    val request = ScriptsPainlessExecuteRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body
    )

    scriptsPainlessExecute(request)

  }

  def scriptsPainlessExecute(
    request: ScriptsPainlessExecuteRequest
  ): ZIO[Any, FrameworkException, ScriptsPainlessExecuteResponse] =
    httpService.execute[ScriptsPainlessExecuteRequestBody, ScriptsPainlessExecuteResponse](request)

  /*
   * Allows to retrieve a large numbers of results from a single search request.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-body.html#request-body-search-scroll
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
   * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
   * @param scrollId The scroll ID for scrolled search
   */
  def scroll(
    body: ScrollRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    restTotalHitsAsInt: Boolean = false
  ): ZIO[Any, FrameworkException, ScrollResponse] = {
    val request = ScrollRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      restTotalHitsAsInt = restTotalHitsAsInt
    )

    scroll(request)

  }

  def scroll(request: ScrollRequest): ZIO[Any, FrameworkException, ScrollResponse] =
    httpService.execute[ScrollRequestBody, ScrollResponse](request)

  /*
   * Returns results matching a query.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-search.html
   *
   * @param index Comma-separated list of data streams, indices,
   * and aliases to search. Supports wildcards (*).

   * @param profile
@server_default false

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
   * @param forceSyntheticSource Should this request force synthetic _source? Use this to test if the mapping supports synthetic _source and to get a sense of the worst case performance. Fetches with this enabled will be slower the enabling synthetic source natively in the index.
   * @param from Starting offset (default: 0)
   * @param ignoreThrottled Whether specified concrete, expanded or aliased indices should be ignored when throttled
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param maxConcurrentShardRequests The number of concurrent shard requests per node this search executes concurrently. This value should be used to limit the impact of the search on the cluster in order to limit the number of concurrent shard requests
   * @param minCompatibleShardNode The minimum compatible version that all shards involved in search should have for this request to be successful
   * @param preFilterShardSize A threshold that enforces a pre-filter roundtrip to prefilter search shards based on query rewriting if the number of shards the search request expands to exceeds the threshold. This filter roundtrip can limit the number of shards significantly if for instance a shard can not match any documents based on its rewrite method ie. if date filters are mandatory to match but the shard bounds and the query are disjoint.
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
   * @param trackTotalHits Indicate if the number of documents that match the query should be tracked. A number can also be specified, to accurately track the total hit count up to the number.
   * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
   * @param version Specify whether to return document version as part of a hit
   */
  def search(
    body: SearchRequestBody,
    index: Chunk[String] = Chunk.empty[String],
    profile: Option[Boolean] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    allowPartialSearchResults: Boolean = true,
    analyzeWildcard: Option[Boolean] = None,
    analyzer: Option[String] = None,
    batchedReduceSize: Double = 512,
    ccsMinimizeRoundtrips: Boolean = true,
    defaultOperator: DefaultOperator = DefaultOperator.OR,
    df: Option[String] = None,
    docvalueFields: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    explain: Option[Boolean] = None,
    forceSyntheticSource: Option[Boolean] = None,
    from: Option[Double] = None,
    ignoreThrottled: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    lenient: Option[Boolean] = None,
    maxConcurrentShardRequests: Double = 5,
    minCompatibleShardNode: Option[String] = None,
    preFilterShardSize: Option[Double] = None,
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
    suggestSize: Option[Double] = None,
    suggestText: Option[String] = None,
    terminateAfter: Option[Long] = None,
    timeout: Option[String] = None,
    trackScores: Option[Boolean] = None,
    trackTotalHits: Option[Long] = None,
    typedKeys: Option[Boolean] = None,
    version: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, SearchResponse] = {
    val request = SearchRequest(
      index = index,
      profile = profile,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      allowPartialSearchResults = allowPartialSearchResults,
      analyzeWildcard = analyzeWildcard,
      analyzer = analyzer,
      batchedReduceSize = batchedReduceSize,
      body = body,
      ccsMinimizeRoundtrips = ccsMinimizeRoundtrips,
      defaultOperator = defaultOperator,
      df = df,
      docvalueFields = docvalueFields,
      expandWildcards = expandWildcards,
      explain = explain,
      forceSyntheticSource = forceSyntheticSource,
      from = from,
      ignoreThrottled = ignoreThrottled,
      ignoreUnavailable = ignoreUnavailable,
      indices = indices,
      lenient = lenient,
      maxConcurrentShardRequests = maxConcurrentShardRequests,
      minCompatibleShardNode = minCompatibleShardNode,
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

    search(request)

  }

  def search(request: SearchRequest): ZIO[Any, FrameworkException, SearchResponse] =
    httpService.execute[SearchRequestBody, SearchResponse](request)

  /*
   * Searches a vector tile for geospatial values. Returns results as a binary Mapbox vector tile.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-vector-tile-api.html
   *
   * @param index Comma-separated list of data streams, indices, or aliases to search
   * @param field Field containing geospatial data to return
   * @param zoom Zoom level for the vector tile to search
   * @param x X coordinate for the vector tile to search
   * @param y Y coordinate for the vector tile to search
   * @param gridAgg Aggregation used to create a grid for `field`.

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param exactBounds If false, the meta layer's feature is the bounding box of the tile. If true, the meta layer's feature is a bounding box resulting from a `geo_bounds` aggregation.
   * @param extent Size, in pixels, of a side of the vector tile.
   * @param gridPrecision Additional zoom levels available through the aggs layer. Accepts 0-8.
   * @param gridType Determines the geometry type for features in the aggs layer.
   * @param size Maximum number of features to return in the hits layer. Accepts 0-10000.
   * @param trackTotalHits Indicate if the number of documents that match the query should be tracked. A number can also be specified, to accurately track the total hit count up to the number.
   * @param withLabels If true, the hits and aggs layers will contain additional point features with suggested label positions for the original features.
   */
  def searchMvt(
    index: String,
    field: String,
    zoom: String,
    x: String,
    y: String,
    body: SearchMvtRequestBody = SearchMvtRequestBody(),
    gridAgg: GridAggregationType,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    exactBounds: Boolean = false,
    extent: Int = 4096,
    gridPrecision: Int = 8,
    gridType: GridType = GridType.grid,
    size: Int = 10000,
    trackTotalHits: Option[Long] = None,
    withLabels: Boolean = false
  ): ZIO[Any, FrameworkException, SearchMvtResponse] = {
    val request = SearchMvtRequest(
      index = index,
      field = field,
      zoom = zoom,
      x = x,
      y = y,
      gridAgg = gridAgg,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      exactBounds = exactBounds,
      extent = extent,
      gridPrecision = gridPrecision,
      gridType = gridType,
      size = size,
      trackTotalHits = trackTotalHits,
      withLabels = withLabels
    )

    searchMvt(request)

  }

  def searchMvt(request: SearchMvtRequest): ZIO[Any, FrameworkException, SearchMvtResponse] =
    httpService.execute[SearchMvtRequestBody, SearchMvtResponse](request)

  /*
   * Returns information about the indices and shards that a search request would be executed against.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-shards.html
   *
   * @param index

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param routing Specific routing value
   */
  def searchShards(
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    local: Option[Boolean] = None,
    preference: Option[String] = None,
    routing: Option[String] = None
  ): ZIO[Any, FrameworkException, SearchShardsResponse] = {
    val request = SearchShardsRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      indices = indices,
      local = local,
      preference = preference,
      routing = routing
    )

    searchShards(request)

  }

  def searchShards(request: SearchShardsRequest): ZIO[Any, FrameworkException, SearchShardsResponse] =
    httpService.execute[Json, SearchShardsResponse](request)

  /*
   * Allows to use the Mustache language to pre-render a search definition.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-template.html
   *
   * @param index Comma-separated list of data streams, indices,
   * and aliases to search. Supports wildcards (*).

   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
    index: Chunk[String],
    body: SearchTemplateRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
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
  ): ZIO[Any, FrameworkException, SearchTemplateResponse] = {
    val request = SearchTemplateRequest(
      index = index,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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

    searchTemplate(request)

  }

  def searchTemplate(request: SearchTemplateRequest): ZIO[Any, FrameworkException, SearchTemplateResponse] =
    httpService.execute[SearchTemplateRequestBody, SearchTemplateResponse](request)

  /*
   * Semantic search API using dense vector similarity
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/semantic-search.html
   *
   * @param indices A comma-separated list of index names to search; use `_all` to perform the operation on all indices
   * @param body body the body of the call
   * @param routing A comma-separated list of specific routing values
   */
  def semanticSearch(
    indices: Seq[String] = Nil,
    body: Json = Json.Null,
    routing: Seq[String] = Nil
  ): ZIO[Any, FrameworkException, SemanticSearchResponse] = {
    val request = SemanticSearchRequest(indices = indices, body = body, routing = routing)

    semanticSearch(request)

  }

  def semanticSearch(request: SemanticSearchRequest): ZIO[Any, FrameworkException, SemanticSearchResponse] =
    httpService.execute[Json, SemanticSearchResponse](request)

  /*
   * The terms enum API  can be used to discover terms in the index that begin with the provided string. It is designed for low-latency look-ups used in auto-complete scenarios.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-terms-enum.html
   *
   * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param index Comma-separated list of data streams, indices, and index aliases to search. Wildcard (*) expressions are supported.

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   */
  def termsEnum(
    indices: Seq[String] = Nil,
    body: TermsEnumRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, TermsEnumResponse] = {
    val request = TermsEnumRequest(
      indices = indices,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body
    )

    termsEnum(request)

  }

  def termsEnum(request: TermsEnumRequest): ZIO[Any, FrameworkException, TermsEnumResponse] =
    httpService.execute[TermsEnumRequestBody, TermsEnumResponse](request)

  /*
   * Returns information and statistics about terms in the fields of a particular document.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-termvectors.html
   *
   * @param index The index in which the document resides.
   * @param id The id of the document, when not specified a doc param should be supplied.
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
    body: TermvectorsRequestBody = TermvectorsRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
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
  ): ZIO[Any, FrameworkException, TermvectorsResponse] = {
    val request = TermvectorsRequest(
      index = index,
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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

    termvectors(request)

  }

  def termvectors(request: TermvectorsRequest): ZIO[Any, FrameworkException, TermvectorsResponse] =
    httpService.execute[TermvectorsRequestBody, TermvectorsResponse](request)

  /*
   * Updates a document with a script or partial document.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-update.html
   *
   * @param index The name of the index
   * @param id Document ID
   * @param body body the body of the call
   * @param taskId

   * @param requestsPerSecond

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param ifPrimaryTerm only perform the update operation if the last operation that has changed the document has the specified primary term
   * @param ifSeqNo only perform the update operation if the last operation that has changed the document has the specified sequence number
   * @param lang The script language (default: painless)
   * @param pipeline The pipeline id to preprocess incoming documents with
   * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
   * @param requireAlias When true, requires destination is an alias. Default is false
   * @param retryOnConflict Specify how many times should the operation be retried when a conflict occurs (default: 0)
   * @param routing Specific routing value
   * @param source True or false to return the _source field or not, or a list of fields to return
   * @param sourceExcludes A list of fields to exclude from the returned _source field
   * @param sourceIncludes A list of fields to extract and return from the _source field
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the update operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def update(
    index: String,
    id: String,
    body: Json,
    taskId: String,
    requestsPerSecond: Float,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    ifPrimaryTerm: Option[Double] = None,
    ifSeqNo: Option[Double] = None,
    lang: Option[String] = None,
    pipeline: Option[String] = None,
    refresh: Option[Refresh] = None,
    requireAlias: Option[Boolean] = None,
    retryOnConflict: Option[Double] = None,
    routing: Option[String] = None,
    source: Seq[String] = Nil,
    sourceExcludes: Seq[String] = Nil,
    sourceIncludes: Seq[String] = Nil,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None
  ): ZIO[Any, FrameworkException, UpdateResponse] = {
    val request = UpdateRequest(
      index = index,
      id = id,
      body = body,
      taskId = taskId,
      requestsPerSecond = requestsPerSecond,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      ifPrimaryTerm = ifPrimaryTerm,
      ifSeqNo = ifSeqNo,
      lang = lang,
      pipeline = pipeline,
      refresh = refresh,
      requireAlias = requireAlias,
      retryOnConflict = retryOnConflict,
      routing = routing,
      source = source,
      sourceExcludes = sourceExcludes,
      sourceIncludes = sourceIncludes,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards
    )

    update(request)

  }

  def update(request: UpdateRequest): ZIO[Any, FrameworkException, UpdateResponse] =
    httpService.execute[Json, UpdateResponse](request)

  /*
   * Performs an update on every document in the index without changing the source, for example to pick up a mapping change.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-update-by-query.html
   *
   * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param taskId

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

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
   * @param refresh Should the affected indexes be refreshed?
   * @param requestCache Specify if request cache should be used for this request or not, defaults to index level setting
   * @param requestsPerSecond The throttle to set on this request in sub-requests per second. -1 means no throttle.
   * @param routing A comma-separated list of specific routing values
   * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
   * @param scrollSize Size on the scroll request powering the update by query
   * @param searchTimeout Explicit timeout for each search request. Defaults to no timeout.
   * @param searchType Search operation type
   * @param slices The number of slices this task should be divided into. Defaults to 1, meaning the task isn't sliced into subtasks. Can be set to `auto`.
   * @param sort A comma-separated list of <field>:<direction> pairs
   * @param stats Specific 'tag' of the request for logging and statistical purposes
   * @param terminateAfter The maximum number of documents to collect for each shard, upon reaching which the query execution will terminate early.
   * @param timeout Time each individual bulk request should wait for shards that are unavailable.
   * @param version Specify whether to return document version as part of a hit
   * @param versionType Should the document increment the version number (internal) on hit or not (reindex)
   * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the update by query operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   * @param waitForCompletion Should the request should block until the update by query operation is complete.
   */
  def updateByQuery(
    taskId: String,
    indices: Seq[String] = Nil,
    body: Json = Json.Null,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
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
    pipeline: Option[String] = None,
    preference: Option[String] = None,
    q: Option[String] = None,
    refresh: Option[Boolean] = None,
    requestCache: Option[Boolean] = None,
    requestsPerSecond: Double = 0,
    routing: Seq[String] = Nil,
    scroll: Option[String] = None,
    scrollSize: Double = 100,
    searchTimeout: Option[String] = None,
    searchType: Option[SearchType] = None,
    slices: String = "1",
    sort: Seq[String] = Nil,
    stats: Seq[String] = Nil,
    terminateAfter: Option[Long] = None,
    timeout: String = "1m",
    version: Option[Boolean] = None,
    versionType: Option[Boolean] = None,
    waitForActiveShards: Option[String] = None,
    waitForCompletion: Boolean = true
  ): ZIO[Any, FrameworkException, UpdateByQueryResponse] = {
    val request = UpdateByQueryRequest(
      indices = indices,
      taskId = taskId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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
      stats = stats,
      terminateAfter = terminateAfter,
      timeout = timeout,
      version = version,
      versionType = versionType,
      waitForActiveShards = waitForActiveShards,
      waitForCompletion = waitForCompletion
    )

    updateByQuery(request)

  }

  def updateByQuery(request: UpdateByQueryRequest): ZIO[Any, FrameworkException, UpdateByQueryResponse] =
    httpService.execute[Json, UpdateByQueryResponse](request)

  /*
   * Changes the number of requests per second for a particular Update By Query operation.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update-by-query.html
   *
   * @param requestsPerSecond The throttle to set on this request in floating sub-requests per second. -1 means set no throttle.
   * @param taskId The task id to rethrottle
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def updateByQueryRethrottle(
    requestsPerSecond: Int,
    taskId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, UpdateByQueryRethrottleResponse] = {
    val request = UpdateByQueryRethrottleRequest(
      requestsPerSecond = requestsPerSecond,
      taskId = taskId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    updateByQueryRethrottle(request)

  }

  def updateByQueryRethrottle(
    request: UpdateByQueryRethrottleRequest
  ): ZIO[Any, FrameworkException, UpdateByQueryRethrottleResponse] =
    httpService.execute[Json, UpdateByQueryRethrottleResponse](request)

}
