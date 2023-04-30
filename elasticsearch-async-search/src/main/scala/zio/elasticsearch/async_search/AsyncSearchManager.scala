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

package zio.elasticsearch.async_search

import zio._
import zio.elasticsearch._
import zio.elasticsearch.async_search.delete.{DeleteRequest, DeleteResponse}
import zio.elasticsearch.async_search.get.{GetRequest, GetResponse}
import zio.elasticsearch.async_search.requests.SubmitRequestBody
import zio.elasticsearch.async_search.status.{StatusRequest, StatusResponse}
import zio.elasticsearch.async_search.submit.{SubmitRequest, SubmitResponse}
import zio.elasticsearch.common._
import zio.elasticsearch.common.search.SearchType
import zio.exception._
import zio.json.ast._

object AsyncSearchManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, AsyncSearchManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new AsyncSearchManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait AsyncSearchManager {
  def httpService: ElasticSearchHttpService

  /*
   * Deletes an async search by ID. If the search is still running, the search request will be cancelled. Otherwise, the saved search results are deleted.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/async-search.html
   *
   * @param id The async search ID
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
  def delete(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, DeleteResponse] = {
    val request =
      DeleteRequest(id = id, errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    delete(request)

  }

  def delete(request: DeleteRequest): ZIO[Any, FrameworkException, DeleteResponse] =
    httpService.execute[Json, DeleteResponse](request)

  /*
   * Retrieves the results of a previously submitted async search request given its ID.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/async-search.html
   *
   * @param id The async search ID
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

   * @param keepAlive Specify the time interval in which the results (partial or final) for this search will be available
   * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
   * @param waitForCompletionTimeout Specify the time that the request should block waiting for the final response
   */
  def get(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    keepAlive: Option[String] = None,
    typedKeys: Option[Boolean] = None,
    waitForCompletionTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, GetResponse] = {
    val request = GetRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      keepAlive = keepAlive,
      typedKeys = typedKeys,
      waitForCompletionTimeout = waitForCompletionTimeout
    )

    get(request)

  }

  def get(request: GetRequest): ZIO[Any, FrameworkException, GetResponse] =
    httpService.execute[Json, GetResponse](request)

  /*
   * Retrieves the status of a previously submitted async search request given its ID.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/async-search.html
   *
   * @param id The async search ID
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
  def status(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, StatusResponse] = {
    val request =
      StatusRequest(id = id, errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    status(request)

  }

  def status(request: StatusRequest): ZIO[Any, FrameworkException, StatusResponse] =
    httpService.execute[Json, StatusResponse](request)

  /*
   * Executes a search request asynchronously.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/async-search.html
   *
   * @param index

   * @param ccsMinimizeRoundtrips

   * @param minCompatibleShardNode

   * @param preFilterShardSize

   * @param scroll

   * @param restTotalHitsAsInt

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
   * @param batchedReduceSize The number of shard results that should be reduced at once on the coordinating node. This value should be used as the granularity at which progress results will be made available.
   * @param body body the body of the call
   * @param defaultOperator The default operator for query string query (AND or OR)
   * @param df The field to use as default where no field prefix is given in the query string
   * @param docvalueFields A comma-separated list of fields to return as the docvalue representation of a field for each hit
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param explain Specify whether to return detailed information about score computation as part of a hit
   * @param from Starting offset (default: 0)
   * @param ignoreThrottled Whether specified concrete, expanded or aliased indices should be ignored when throttled
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
   * @param keepAlive Update the time interval in which the results (partial or final) for this search will be available
   * @param keepOnCompletion Control whether the response should be stored in the cluster if it completed within the provided [wait_for_completion] time (default: false)
   * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
   * @param maxConcurrentShardRequests The number of concurrent shard requests per node this search executes concurrently. This value should be used to limit the impact of the search on the cluster in order to limit the number of concurrent shard requests
   * @param preference Specify the node or shard the operation should be performed on (default: random)
   * @param q Query in the Lucene query string syntax
   * @param requestCache Specify if request cache should be used for this request or not, defaults to true
   * @param routing A comma-separated list of specific routing values
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
   * @param waitForCompletionTimeout Specify the time that the request should block waiting for the final response
   */
  def submit(
    index: Chunk[String],
    body: SubmitRequestBody,
    ccsMinimizeRoundtrips: Boolean,
    minCompatibleShardNode: String,
    preFilterShardSize: Long,
    scroll: String,
    restTotalHitsAsInt: Boolean,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    allowPartialSearchResults: Boolean = true,
    analyzeWildcard: Option[Boolean] = None,
    analyzer: Option[String] = None,
    batchedReduceSize: Double = 5,
    defaultOperator: DefaultOperator = DefaultOperator.OR,
    df: Option[String] = None,
    docvalueFields: Chunk[String] = Chunk.empty,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    explain: Option[Boolean] = None,
    from: Option[Double] = None,
    ignoreThrottled: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Chunk[String] = Chunk.empty,
    keepAlive: String = "5d",
    keepOnCompletion: Boolean = false,
    lenient: Option[Boolean] = None,
    maxConcurrentShardRequests: Double = 5,
    preference: Option[String] = None,
    q: Option[String] = None,
    requestCache: Option[Boolean] = None,
    routing: Chunk[String] = Chunk.empty,
    searchType: Option[SearchType] = None,
    seqNoPrimaryTerm: Option[Boolean] = None,
    size: Option[Double] = None,
    sort: Chunk[String] = Chunk.empty,
    source: Chunk[String] = Chunk.empty,
    sourceExcludes: Chunk[String] = Chunk.empty,
    sourceIncludes: Chunk[String] = Chunk.empty,
    stats: Chunk[String] = Chunk.empty,
    storedFields: Chunk[String] = Chunk.empty,
    suggestField: Option[String] = None,
    suggestMode: SuggestMode = SuggestMode.missing,
    suggestSize: Option[Double] = None,
    suggestText: Option[String] = None,
    terminateAfter: Option[Double] = None,
    timeout: Option[String] = None,
    trackScores: Option[Boolean] = None,
    trackTotalHits: Option[Long] = None,
    typedKeys: Option[Boolean] = None,
    version: Option[Boolean] = None,
    waitForCompletionTimeout: String = "1s"
  ): ZIO[Any, FrameworkException, SubmitResponse] = {
    val request = SubmitRequest(
      index = index,
      ccsMinimizeRoundtrips = ccsMinimizeRoundtrips,
      minCompatibleShardNode = minCompatibleShardNode,
      preFilterShardSize = preFilterShardSize,
      scroll = scroll,
      restTotalHitsAsInt = restTotalHitsAsInt,
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
      defaultOperator = defaultOperator,
      df = df,
      docvalueFields = docvalueFields,
      expandWildcards = expandWildcards,
      explain = explain,
      from = from,
      ignoreThrottled = ignoreThrottled,
      ignoreUnavailable = ignoreUnavailable,
      indices = indices,
      keepAlive = keepAlive,
      keepOnCompletion = keepOnCompletion,
      lenient = lenient,
      maxConcurrentShardRequests = maxConcurrentShardRequests,
      preference = preference,
      q = q,
      requestCache = requestCache,
      routing = routing,
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
      version = version,
      waitForCompletionTimeout = waitForCompletionTimeout
    )

    submit(request)

  }

  def submit(request: SubmitRequest): ZIO[Any, FrameworkException, SubmitResponse] =
    httpService.execute[SubmitRequestBody, SubmitResponse](request)

}
