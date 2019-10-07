/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests
import io.circe._
import io.circe.syntax._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

import scala.collection.mutable
import elasticsearch.Conflicts
import _root_.elasticsearch.queries.DefaultOperator
import elasticsearch.SearchType
import elasticsearch.ExpandWildcards
import elasticsearch.queries.Query
import elasticsearch.script.Script

/*
 * https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-update-by-query.html
 *
 * @param indices A list of index names to search; use `_all` or empty string to perform the operation on all indices
 * @param docTypes A list of document types to search; leave empty to perform the operation on all types
 * @param body body the body of the call
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param sourceInclude A list of fields to extract and return from the _source field
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param requestCache Specify if request cache should be used for this request or not, defaults to index level setting
 * @param refresh Should the effected indexes be refreshed?
 * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
 * @param stats Specific 'tag' of the request for logging and statistical purposes
 * @param analyzer The analyzer to use for the query string
 * @param size Number of hits to return (default: 10)
 * @param searchType Search operation type
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param sourceExclude A list of fields to exclude from the returned _source field
 * @param defaultOperator The default operator for query string query (AND or OR)
 * @param scrollSize Size on the scroll request powering the update_by_query
 * @param terminateAfter The maximum number of documents to collect for each shard, upon reaching which the query execution will terminate early.
 * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
 * @param conflicts What to do when the update by query hits version conflicts?
 * @param pipeline Ingest pipeline to set on index requests made by this action. (default: none)
 * @param version Specify whether to return document version as part of a hit
 * @param q Query in the Lucene query string syntax
 * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
 * @param versionType Should the document increment the version number (internal) on hit or not (reindex)
 * @param from Starting offset (default: 0)
 * @param df The field to use as default where no field prefix is given in the query string
 * @param routing A list of specific routing values
 * @param searchTimeout Explicit timeout for each search request. Defaults to no timeout.
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param waitForCompletion Should the request should block until the update by query operation is complete.
 * @param slices The number of slices this task should be divided into. Defaults to 1 meaning the task isn't sliced into subtasks.
 * @param sort A list of <field>:<direction> pairs
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param requestsPerSecond The throttle to set on this request in sub-requests per second. -1 means no throttle.
 * @param timeout Time each individual bulk request should wait for shards that are unavailable.
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the update by query operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */
@JsonCodec
final case class UpdateByQueryRequest(
  indices: Seq[String] = Nil,
  body: JsonObject = JsonObject.empty,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @JsonKey("_source_include") sourceInclude: Seq[String] = Nil,
  @JsonKey("_source") source: Seq[String] = Nil,
  @JsonKey("request_cache") requestCache: Option[Boolean] = None,
  refresh: Option[Boolean] = None,
  scroll: Option[String] = None,
  stats: Seq[String] = Nil,
  analyzer: Option[String] = None,
  size: Double = 10,
  @JsonKey("search_type") searchType: Option[SearchType] = None,
  preference: String = "random",
  @JsonKey("_source_exclude") sourceExclude: Seq[String] = Nil,
  @JsonKey("default_operator") defaultOperator: DefaultOperator = DefaultOperator.OR,
  @JsonKey("scroll_size") scrollSize: Option[Double] = None,
  @JsonKey("terminate_after") terminateAfter: Option[Double] = None,
  @JsonKey("analyze_wildcard") analyzeWildcard: Boolean = false,
  conflicts: Seq[Conflicts] = Nil,
  pipeline: String = "none",
  version: Option[Boolean] = None,
  q: Option[String] = None,
  lenient: Option[Boolean] = None,
  @JsonKey("version_type") versionType: Option[Boolean] = None,
  from: Double = 0,
  df: Option[String] = None,
  routing: Seq[String] = Nil,
  @JsonKey("search_timeout") searchTimeout: Option[String] = None,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @JsonKey("wait_for_completion") waitForCompletion: Boolean = false,
  slices: Double = 1,
  sort: Seq[String] = Nil,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  @JsonKey("requests_per_second") requestsPerSecond: Double = 0,
  timeout: String = "1m",
  @JsonKey("wait_for_active_shards") waitForActiveShards: Option[String] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(indices, "_update_by_query")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!expandWildcards.isEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

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
    refresh.map { v =>
      queryArgs += ("refresh" -> v.toString)
    }
    scroll.map { v =>
      queryArgs += ("scroll" -> v.toString)
    }
    if (!stats.isEmpty) {
      queryArgs += ("stats" -> stats.toList.mkString(","))
    }
    analyzer.map { v =>
      queryArgs += ("analyzer" -> v)
    }
    if (size != 10) queryArgs += ("size" -> size.toString)
    searchType.map { v =>
      queryArgs += ("search_type" -> v.toString)
    }
    if (preference != "random") queryArgs += ("preference" -> preference)
    if (!sourceExclude.isEmpty) {
      queryArgs += ("_source_exclude" -> sourceExclude.toList.mkString(","))
    }
    if (defaultOperator != DefaultOperator.OR)
      queryArgs += ("default_operator" -> defaultOperator.toString)
    scrollSize.map { v =>
      queryArgs += ("scroll_size" -> v.toString)
    }
    terminateAfter.map { v =>
      queryArgs += ("terminate_after" -> v.toString)
    }
    if (analyzeWildcard != false)
      queryArgs += ("analyze_wildcard" -> analyzeWildcard.toString)
    if (!conflicts.isEmpty) {
      if (conflicts.toSet != Set(Conflicts.abort)) {
        queryArgs += ("conflicts" -> conflicts.mkString(","))
      }

    }
    if (pipeline != "none") queryArgs += ("pipeline" -> pipeline)
    version.map { v =>
      queryArgs += ("version" -> v.toString)
    }
    q.map { v =>
      queryArgs += ("q" -> v)
    }
    lenient.map { v =>
      queryArgs += ("lenient" -> v.toString)
    }
    versionType.map { v =>
      queryArgs += ("version_type" -> v.toString)
    }
    if (from != 0) queryArgs += ("from" -> from.toString)
    df.map { v =>
      queryArgs += ("df" -> v)
    }
    if (!routing.isEmpty) {
      queryArgs += ("routing" -> routing.toList.mkString(","))
    }
    searchTimeout.map { v =>
      queryArgs += ("search_timeout" -> v.toString)
    }
    allowNoIndices.map { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    if (waitForCompletion != false)
      queryArgs += ("wait_for_completion" -> waitForCompletion.toString)
    if (slices != 1) queryArgs += ("slices" -> slices.toString)
    if (!sort.isEmpty) {
      queryArgs += ("sort" -> sort.toList.mkString(","))
    }
    ignoreUnavailable.map { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    if (requestsPerSecond != 0)
      queryArgs += ("requests_per_second" -> requestsPerSecond.toString)
    if (timeout != "1m") queryArgs += ("timeout" -> timeout.toString)
    waitForActiveShards.map { v =>
      queryArgs += ("wait_for_active_shards" -> v)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  def setQuery(query: Query): UpdateByQueryRequest =
    this.copy(body = body.add("query", query.asJson))

  def setScript(script: Script): UpdateByQueryRequest =
    this.copy(body = body.add("script", script.asJson))

  def setPartialDoc(doc: JsonObject): UpdateByQueryRequest =
    setScript(
      Script(
        """for(entry in params.entrySet()){
                  if(!entry.getKey().equals("ctx")){
                    ctx._source.put(entry.getKey(), entry.getValue());
                  }
                }""",
        doc
      )
    )

  // Custom Code Off

}

object UpdateByQueryRequest {

  def fromPartialDocument(index: String, doc: JsonObject): UpdateByQueryRequest =
    UpdateByQueryRequest(indices = Seq(index)).setPartialDoc(doc)
}
