/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import _root_.elasticsearch.queries.DefaultOperator
import elasticsearch.{ ExpandWildcards, SearchType, SuggestMode }
import io.circe._
import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey

import scala.collection.mutable

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-search.html
 *
 * @param body body the body of the call
 * @param indices A list of index names to search; use `_all` or empty string to perform the operation on all indices
 * @param docTypes A list of document types to search; leave empty to perform the operation on all types
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
 * @param sourceInclude A list of fields to extract and return from the _source field
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param requestCache Specify if request cache should be used for this request or not, defaults to index level setting
 * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
 * @param stats Specific 'tag' of the request for logging and statistical purposes
 * @param analyzer The analyzer to use for the query string
 * @param size Number of hits to return (default: 10)
 * @param explain Specify whether to return detailed information about score computation as part of a hit
 * @param searchType Search operation type
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param fielddataFields A list of fields to return as the docvalue representation of a field for each hit
 * @param sourceExclude A list of fields to exclude from the returned _source field
 * @param defaultOperator The default operator for query string query (AND or OR)
 * @param terminateAfter The maximum number of documents to collect for each shard, upon reaching which the query execution will terminate early.
 * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
 * @param batchedReduceSize The number of shard results that should be reduced at once on the coordinating node. This value should be used as a protection mechanism to reduce the memory overhead per search request if the potential number of shards in the request can be large.
 * @param suggestMode Specify suggest mode
 * @param version Specify whether to return document version as part of a hit
 * @param suggestText The source text for which the suggestions should be returned
 * @param trackScores Whether to calculate and return scores even if they are not used for sorting
 * @param q Query in the Lucene query string syntax
 * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
 * @param from Starting offset (default: 0)
 * @param suggestSize How many suggestions to return in response
 * @param df The field to use as default where no field prefix is given in the query string
 * @param routing A list of specific routing values
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param sort A list of <field>:<direction> pairs
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param timeout Explicit operation timeout
 * @param storedFields A list of stored fields to return as part of a hit
 * @param suggestField Specify which field to use for suggestions
 * @param docvalueFields A list of fields to return as the docvalue representation of a field for each hit
 */
@JsonCodec
final case class SearchRequest(
  body: Json,
  indices: Seq[String] = Nil,
  docTypes: Seq[String] = Nil,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @JsonKey("typed_keys") typedKeys: Option[Boolean] = None,
  @JsonKey("_source_include") sourceInclude: Seq[String] = Nil,
  @JsonKey("_source") source: Seq[String] = Nil,
  @JsonKey("request_cache") requestCache: Option[Boolean] = None,
  scroll: Option[String] = None,
  stats: Seq[String] = Nil,
  analyzer: Option[String] = None,
  size: Int = 10,
  explain: Option[Boolean] = None,
  @JsonKey("search_type") searchType: Option[SearchType] = None,
  preference: String = "random",
  @JsonKey("fielddata_fields") fielddataFields: Seq[String] = Nil,
  @JsonKey("_source_exclude") sourceExclude: Seq[String] = Nil,
  @JsonKey("default_operator") defaultOperator: DefaultOperator = DefaultOperator.OR,
  @JsonKey("terminate_after") terminateAfter: Option[Double] = None,
  @JsonKey("analyze_wildcard") analyzeWildcard: Boolean = false,
  @JsonKey("batched_reduce_size") batchedReduceSize: Int = 512,
  @JsonKey("suggest_mode") suggestMode: SuggestMode = SuggestMode.missing,
  version: Option[Boolean] = None,
  @JsonKey("suggest_text") suggestText: Option[String] = None,
  @JsonKey("track_scores") trackScores: Option[Boolean] = None,
  q: Option[String] = None,
  lenient: Option[Boolean] = None,
  from: Int = 0,
  @JsonKey("suggest_size") suggestSize: Option[Int] = None,
  df: Option[String] = None,
  routing: Option[String] = None,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  sort: Seq[String] = Nil,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  timeout: Option[String] = None,
  @JsonKey("stored_fields") storedFields: Seq[String] = Nil,
  @JsonKey("suggest_field") suggestField: Option[String] = None,
  @JsonKey("docvalue_fields") docvalueFields: Seq[String] = Nil
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(indices, docTypes, "_search")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (expandWildcards.nonEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    typedKeys.map { v =>
      queryArgs += ("typed_keys" -> v.toString)
    }
    if (!sourceInclude.isEmpty) {
      queryArgs += ("_source_include" -> sourceInclude.toList.mkString(","))
    }
    if (!source.isEmpty) {
      queryArgs += ("_source" -> source.toList.mkString(","))
    }
    requestCache.map { v =>
      queryArgs += ("request_cache" -> v.toString)
    }
    scroll.map { v =>
      queryArgs += ("scroll" -> v.toString)
    }
    if (stats.nonEmpty) {
      queryArgs += ("stats" -> stats.toList.mkString(","))
    }
    analyzer.map { v =>
      queryArgs += ("analyzer" -> v)
    }
    if (size != 10) queryArgs += ("size" -> size.toString)
    explain.map { v =>
      queryArgs += ("explain" -> v.toString)
    }
    searchType.foreach {
      case v =>
        queryArgs += ("search_type" -> v.toString)
    }
    if (preference != "random") queryArgs += ("preference" -> preference)
    if (fielddataFields.nonEmpty) {
      queryArgs += ("fielddata_fields" -> fielddataFields.toList.mkString(","))
    }
    if (sourceExclude.nonEmpty) {
      queryArgs += ("_source_exclude" -> sourceExclude.toList.mkString(","))
    }
    if (defaultOperator != DefaultOperator.OR)
      queryArgs += ("default_operator" -> defaultOperator.toString)
    terminateAfter.map { v =>
      queryArgs += ("terminate_after" -> v.toString)
    }
    if (analyzeWildcard)
      queryArgs += ("analyze_wildcard" -> analyzeWildcard.toString)
    if (batchedReduceSize != 512)
      queryArgs += ("batched_reduce_size" -> batchedReduceSize.toString)
    if (suggestMode != SuggestMode.missing)
      queryArgs += ("suggest_mode" -> suggestMode.toString)
    version.map { v =>
      queryArgs += ("version" -> v.toString)
    }
    suggestText.map { v =>
      queryArgs += ("suggest_text" -> v)
    }
    trackScores.map { v =>
      queryArgs += ("track_scores" -> v.toString)
    }
    q.map { v =>
      queryArgs += ("q" -> v)
    }
    lenient.map { v =>
      queryArgs += ("lenient" -> v.toString)
    }
    if (from != 0) queryArgs += ("from" -> from.toString)
    suggestSize.map { v =>
      queryArgs += ("suggest_size" -> v.toString)
    }
    df.map { v =>
      queryArgs += ("df" -> v)
    }
    if (routing.isDefined) {
      queryArgs += ("routing" -> routing.toList.mkString(","))
    }
    allowNoIndices.map { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    if (sort.nonEmpty) {
      queryArgs += ("sort" -> sort.toList.mkString(","))
    }
    ignoreUnavailable.map { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    if (storedFields.nonEmpty) {
      queryArgs += ("stored_fields" -> storedFields.toList.mkString(","))
    }
    suggestField.map { v =>
      queryArgs += ("suggest_field" -> v)
    }
    if (docvalueFields.nonEmpty) {
      queryArgs += ("docvalue_fields" -> docvalueFields.toList.mkString(","))
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
