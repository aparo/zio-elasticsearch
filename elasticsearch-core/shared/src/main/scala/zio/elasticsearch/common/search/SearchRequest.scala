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

package zio.elasticsearch.common.search
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.common.requests.SearchRequestBody
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

final case class SearchRequest(
  body: SearchRequestBody = SearchRequestBody(),
  index: Chunk[String] = Chunk.empty,
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
  docvalueFields: Chunk[String] = Chunk.empty,
  expandWildcards: Seq[ExpandWildcards] = Nil,
  explain: Option[Boolean] = None,
  forceSyntheticSource: Option[Boolean] = None,
  from: Option[Double] = None,
  ignoreThrottled: Option[Boolean] = None,
  ignoreUnavailable: Option[Boolean] = None,
  indices: Chunk[String] = Chunk.empty,
  lenient: Option[Boolean] = None,
  maxConcurrentShardRequests: Double = 5,
  minCompatibleShardNode: Option[String] = None,
  preFilterShardSize: Option[Double] = None,
  preference: Option[String] = None,
  q: Option[String] = None,
  requestCache: Option[Boolean] = None,
  restTotalHitsAsInt: Boolean = false,
  routing: Chunk[String] = Chunk.empty,
  scroll: Option[String] = None,
  searchType: Option[SearchType] = None,
  seqNoPrimaryTerm: Option[Boolean] = None,
  size: Option[Long] = None,
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
  terminateAfter: Option[Long] = None,
  timeout: Option[String] = None,
  trackScores: Option[Boolean] = None,
  trackTotalHits: Option[Long] = None,
  typedKeys: Option[Boolean] = None,
  version: Option[Boolean] = None
) extends ActionRequest[SearchRequestBody]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl(indices, "_search")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    allowNoIndices.foreach { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    if (allowPartialSearchResults != true)
      queryArgs += ("allow_partial_search_results" -> allowPartialSearchResults.toString)
    analyzeWildcard.foreach { v =>
      queryArgs += ("analyze_wildcard" -> v.toString)
    }
    analyzer.foreach { v =>
      queryArgs += ("analyzer" -> v)
    }
    if (batchedReduceSize != 512)
      queryArgs += ("batched_reduce_size" -> batchedReduceSize.toString)
    if (ccsMinimizeRoundtrips != true)
      queryArgs += ("ccs_minimize_roundtrips" -> ccsMinimizeRoundtrips.toString)
    if (defaultOperator != DefaultOperator.OR)
      queryArgs += ("default_operator" -> defaultOperator.toString)
    df.foreach { v =>
      queryArgs += ("df" -> v)
    }
    if (docvalueFields.nonEmpty) {
      queryArgs += ("docvalue_fields" -> docvalueFields.toList.mkString(","))
    }
    if (expandWildcards.nonEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    explain.foreach { v =>
      queryArgs += ("explain" -> v.toString)
    }
    forceSyntheticSource.foreach { v =>
      queryArgs += ("force_synthetic_source" -> v.toString)
    }
    from.foreach { v =>
      queryArgs += ("from" -> v.toString)
    }
    ignoreThrottled.foreach { v =>
      queryArgs += ("ignore_throttled" -> v.toString)
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    lenient.foreach { v =>
      queryArgs += ("lenient" -> v.toString)
    }
    if (maxConcurrentShardRequests != 5)
      queryArgs += ("max_concurrent_shard_requests" -> maxConcurrentShardRequests.toString)
    minCompatibleShardNode.foreach { v =>
      queryArgs += ("min_compatible_shard_node" -> v)
    }
    preFilterShardSize.foreach { v =>
      queryArgs += ("pre_filter_shard_size" -> v.toString)
    }
    preference.foreach { v =>
      queryArgs += ("preference" -> v)
    }
    q.foreach { v =>
      queryArgs += ("q" -> v)
    }
    requestCache.foreach { v =>
      queryArgs += ("request_cache" -> v.toString)
    }
    if (restTotalHitsAsInt != false)
      queryArgs += ("rest_total_hits_as_int" -> restTotalHitsAsInt.toString)
    if (routing.nonEmpty) {
      queryArgs += ("routing" -> routing.toList.mkString(","))
    }
    scroll.foreach { v =>
      queryArgs += ("scroll" -> v.toString)
    }
    searchType.foreach { v =>
      queryArgs += ("search_type" -> v.toString)
    }
    seqNoPrimaryTerm.foreach { v =>
      queryArgs += ("seq_no_primary_term" -> v.toString)
    }
    size.foreach { v =>
      queryArgs += ("size" -> v.toString)
    }
    if (sort.nonEmpty) {
      queryArgs += ("sort" -> sort.toList.mkString(","))
    }
    if (source.nonEmpty) {
      queryArgs += ("_source" -> source.toList.mkString(","))
    }
    if (sourceExcludes.nonEmpty) {
      queryArgs += ("_source_excludes" -> sourceExcludes.toList.mkString(","))
    }
    if (sourceIncludes.nonEmpty) {
      queryArgs += ("_source_includes" -> sourceIncludes.toList.mkString(","))
    }
    if (stats.nonEmpty) {
      queryArgs += ("stats" -> stats.toList.mkString(","))
    }
    if (storedFields.nonEmpty) {
      queryArgs += ("stored_fields" -> storedFields.toList.mkString(","))
    }
    suggestField.foreach { v =>
      queryArgs += ("suggest_field" -> v)
    }
    if (suggestMode != SuggestMode.missing)
      queryArgs += ("suggest_mode" -> suggestMode.toString)
    suggestSize.foreach { v =>
      queryArgs += ("suggest_size" -> v.toString)
    }
    suggestText.foreach { v =>
      queryArgs += ("suggest_text" -> v)
    }
    terminateAfter.foreach { v =>
      queryArgs += ("terminate_after" -> v.toString)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    trackScores.foreach { v =>
      queryArgs += ("track_scores" -> v.toString)
    }
    trackTotalHits.foreach { v =>
      queryArgs += ("track_total_hits" -> v.toString)
    }
    typedKeys.foreach { v =>
      queryArgs += ("typed_keys" -> v.toString)
    }
    version.foreach { v =>
      queryArgs += ("version" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
