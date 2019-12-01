/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import elasticsearch.{
  DefaultOperator,
  ExpandWildcards,
  SearchType,
  SuggestMode
}
import io.circe._
import io.circe.derivation.annotations.{JsonKey, _}

import scala.collection.mutable

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
@JsonCodec
final case class SearchRequest(
    body: Json,
    @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
    @JsonKey("allow_partial_search_results") allowPartialSearchResults: Boolean =
      true,
    @JsonKey("analyze_wildcard") analyzeWildcard: Option[Boolean] = None,
    analyzer: Option[String] = None,
    @JsonKey("batched_reduce_size") batchedReduceSize: Double = 512,
    @JsonKey("ccs_minimize_roundtrips") ccsMinimizeRoundtrips: Boolean = true,
    @JsonKey("default_operator") defaultOperator: DefaultOperator =
      DefaultOperator.OR,
    df: Option[String] = None,
    @JsonKey("docvalue_fields") docvalueFields: Seq[String] = Nil,
    @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
    explain: Option[Boolean] = None,
    from: Option[Double] = None,
    @JsonKey("ignore_throttled") ignoreThrottled: Option[Boolean] = None,
    @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    lenient: Option[Boolean] = None,
    @JsonKey("max_concurrent_shard_requests") maxConcurrentShardRequests: Double =
      5,
    @JsonKey("pre_filter_shard_size") preFilterShardSize: Double = 128,
    preference: Option[String] = None,
    q: Option[String] = None,
    @JsonKey("request_cache") requestCache: Option[Boolean] = None,
    @JsonKey("rest_total_hits_as_int") restTotalHitsAsInt: Boolean = false,
    routing: Seq[String] = Nil,
    scroll: Option[String] = None,
    @JsonKey("search_type") searchType: Option[SearchType] = None,
    @JsonKey("seq_no_primary_term") seqNoPrimaryTerm: Option[Boolean] = None,
    size: Option[Double] = None,
    sort: Seq[String] = Nil,
    @JsonKey("_source") source: Seq[String] = Nil,
    @JsonKey("_source_excludes") sourceExcludes: Seq[String] = Nil,
    @JsonKey("_source_includes") sourceIncludes: Seq[String] = Nil,
    stats: Seq[String] = Nil,
    @JsonKey("stored_fields") storedFields: Seq[String] = Nil,
    @JsonKey("suggest_field") suggestField: Option[String] = None,
    @JsonKey("suggest_mode") suggestMode: SuggestMode = SuggestMode.missing,
    @JsonKey("suggest_size") suggestSize: Option[Double] = None,
    @JsonKey("suggest_text") suggestText: Option[String] = None,
    @JsonKey("terminate_after") terminateAfter: Option[Long] = None,
    timeout: Option[String] = None,
    @JsonKey("track_scores") trackScores: Option[Boolean] = None,
    @JsonKey("track_total_hits") trackTotalHits: Option[Boolean] = None,
    @JsonKey("typed_keys") typedKeys: Option[Boolean] = None,
    version: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, "_search")

  def queryArgs: Map[String, String] = {
    //managing parameters
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
    if (preFilterShardSize != 128)
      queryArgs += ("pre_filter_shard_size" -> preFilterShardSize.toString)
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
