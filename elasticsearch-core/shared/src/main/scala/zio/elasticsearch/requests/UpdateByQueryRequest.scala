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

package zio.elasticsearch.requests

import zio.elasticsearch.common.{ DefaultOperator, ExpandWildcards }
import scala.collection.mutable

import zio.elasticsearch.queries.Query
import zio.elasticsearch.script.Script
import zio.elasticsearch.{ Conflicts, SearchType }
import zio.json._
import zio.json.ast._
import zio.json._

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
final case class UpdateByQueryRequest(
  body: Json.Obj,
  indices: Seq[String] = Nil,
  @jsonField("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @jsonField("analyze_wildcard") analyzeWildcard: Option[Boolean] = None,
  analyzer: Option[String] = None,
  conflicts: Seq[Conflicts] = Nil,
  @jsonField("default_operator") defaultOperator: DefaultOperator = DefaultOperator.OR,
  df: Option[String] = None,
  @jsonField("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  from: Option[Int] = None,
  @jsonField("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  lenient: Option[Boolean] = None,
  @jsonField("max_docs") maxDocs: Option[Double] = None,
  pipeline: Option[String] = None,
  preference: Option[String] = None,
  q: Option[String] = None,
  refresh: Option[Boolean] = None,
  @jsonField("request_cache") requestCache: Option[Boolean] = None,
  @jsonField("requests_per_second") requestsPerSecond: Int = 0,
  routing: Seq[String] = Nil,
  scroll: Option[String] = None,
  @jsonField("scroll_size") scrollSize: Option[Double] = None,
  @jsonField("search_timeout") searchTimeout: Option[String] = None,
  @jsonField("search_type") searchType: Option[SearchType] = None,
  slices: Option[Int] = None,
  sort: Seq[String] = Nil,
  @jsonField("_source") source: Seq[String] = Nil,
  @jsonField("_source_excludes") sourceExcludes: Seq[String] = Nil,
  @jsonField("_source_includes") sourceIncludes: Seq[String] = Nil,
  stats: Seq[String] = Nil,
  @jsonField("terminate_after") terminateAfter: Option[Long] = None,
  timeout: String = "1m",
  version: Option[Boolean] = None,
  @jsonField("version_type") versionType: Option[Boolean] = None,
  @jsonField("wait_for_active_shards") waitForActiveShards: Option[String] = None,
  @jsonField("wait_for_completion") waitForCompletion: Boolean = true
) extends ActionRequest {
  def method: String = "POST"
  def urlPath: String = this.makeUrl(indices, "_update_by_query")
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    allowNoIndices.foreach { v =>
      queryArgs += "allow_no_indices" -> v.toString
    }
    analyzeWildcard.foreach { v =>
      queryArgs += "analyze_wildcard" -> v.toString
    }
    analyzer.foreach { v =>
      queryArgs += "analyzer" -> v
    }
    if (conflicts.nonEmpty) {
      if (conflicts.toSet != Set(Conflicts.abort)) {
        queryArgs += "conflicts" -> conflicts.mkString(",")
      }
    }
    if (defaultOperator != DefaultOperator.OR) queryArgs += "default_operator" -> defaultOperator.toString
    df.foreach { v =>
      queryArgs += "df" -> v
    }
    if (expandWildcards.nonEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += "expand_wildcards" -> expandWildcards.mkString(",")
      }
    }
    from.foreach { v =>
      queryArgs += "from" -> v.toString
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += "ignore_unavailable" -> v.toString
    }
    lenient.foreach { v =>
      queryArgs += "lenient" -> v.toString
    }
    maxDocs.foreach { v =>
      queryArgs += "max_docs" -> v.toString
    }
    pipeline.foreach { v =>
      queryArgs += "pipeline" -> v
    }
    preference.foreach { v =>
      queryArgs += "preference" -> v
    }
    q.foreach { v =>
      queryArgs += "q" -> v
    }
    refresh.foreach { v =>
      queryArgs += "refresh" -> v.toString
    }
    requestCache.foreach { v =>
      queryArgs += "request_cache" -> v.toString
    }
    if (requestsPerSecond != 0) queryArgs += "requests_per_second" -> requestsPerSecond.toString
    if (routing.nonEmpty) {
      queryArgs += "routing" -> routing.toList.mkString(",")
    }
    scroll.foreach { v =>
      queryArgs += "scroll" -> v.toString
    }
    scrollSize.foreach { v =>
      queryArgs += "scroll_size" -> v.toString
    }
    searchTimeout.foreach { v =>
      queryArgs += "search_timeout" -> v.toString
    }
    searchType.foreach { v =>
      queryArgs += "search_type" -> v.toString
    }
    slices.foreach { v =>
      queryArgs += "slices" -> v.toString
    }
    if (sort.nonEmpty) {
      queryArgs += "sort" -> sort.toList.mkString(",")
    }
    if (source.nonEmpty) {
      queryArgs += "_source" -> source.toList.mkString(",")
    }
    if (sourceExcludes.nonEmpty) {
      queryArgs += "_source_excludes" -> sourceExcludes.toList.mkString(",")
    }
    if (sourceIncludes.nonEmpty) {
      queryArgs += "_source_includes" -> sourceIncludes.toList.mkString(",")
    }
    if (stats.nonEmpty) {
      queryArgs += "stats" -> stats.toList.mkString(",")
    }
    terminateAfter.foreach { v =>
      queryArgs += "terminate_after" -> v.toString
    }
    if (timeout != "1m") queryArgs += "timeout" -> timeout.toString
    version.foreach { v =>
      queryArgs += "version" -> v.toString
    }
    versionType.foreach { v =>
      queryArgs += "version_type" -> v.toString
    }
    waitForActiveShards.foreach { v =>
      queryArgs += "wait_for_active_shards" -> v
    }
    if (waitForCompletion != true) queryArgs += "wait_for_completion" -> waitForCompletion.toString
    queryArgs.toMap
  }
  def setQuery(query: Query): UpdateByQueryRequest = this.copy(body = body.add("query", query.toJsonAST))
  def setScript(script: Script): UpdateByQueryRequest = this.copy(body = body.add("script", script.toJsonAST))
  def setPartialDoc(doc: Json.Obj): UpdateByQueryRequest = setScript(
    Script(
      """for(entry in params.entrySet()){
                  if(!entry.getKey().equals("ctx")){
                    ctx._source.put(entry.getKey(), entry.getValue());
                  }
                }""",
      doc
    )
  )
}

object UpdateByQueryRequest {
  def fromPartialDocument(index: String, doc: Json.Obj): UpdateByQueryRequest =
    UpdateByQueryRequest(body = Json.Obj(), indices = Seq(index)).setPartialDoc(doc)
  implicit val jsonDecoder: JsonDecoder[UpdateByQueryRequest] = DeriveJsonDecoder.gen[UpdateByQueryRequest]
  implicit val jsonEncoder: JsonEncoder[UpdateByQueryRequest] = DeriveJsonEncoder.gen[UpdateByQueryRequest]
}
