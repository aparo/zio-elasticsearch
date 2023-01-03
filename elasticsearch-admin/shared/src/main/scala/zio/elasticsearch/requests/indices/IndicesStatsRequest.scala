/*
 * Copyright 2019 Alberto Paro
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

package zio.elasticsearch.requests.indices

import scala.collection.mutable

import zio.elasticsearch.requests.ActionRequest
import zio.elasticsearch.{ ExpandWildcards, Level }
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Provides statistics on operations happening in an index.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-stats.html
 *
 * @param completionFields A comma-separated list of fields for `fielddata` and `suggest` index metric (supports wildcards)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param fielddataFields A comma-separated list of fields for `fielddata` index metric (supports wildcards)
 * @param fields A comma-separated list of fields for `fielddata` and `completion` index metric (supports wildcards)
 * @param forbidClosedIndices If set to false stats will also collected from closed indices if explicitly specified or if expand_wildcards expands to closed indices
 * @param groups A comma-separated list of search groups for `search` index metric
 * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
 * @param includeUnloadedSegments If set to true segment stats will include stats for segments that are not currently loaded into memory
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param level Return stats aggregated at cluster, index or shard level
 * @param metric Limit the information returned the specific metrics.
 * @param types A comma-separated list of document types for the `indexing` index metric
 */
final case class IndicesStatsRequest(
  @jsonField("completion_fields") completionFields: Seq[String] = Nil,
  @jsonField("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @jsonField("fielddata_fields") fielddataFields: Seq[String] = Nil,
  fields: Seq[String] = Nil,
  @jsonField("forbid_closed_indices") forbidClosedIndices: Boolean = true,
  groups: Seq[String] = Nil,
  @jsonField("include_segment_file_sizes") includeSegmentFileSizes: Boolean = false,
  @jsonField("include_unloaded_segments") includeUnloadedSegments: Boolean = false,
  indices: Seq[String] = Nil,
  level: Level = Level.indices,
  metric: Option[String] = None,
  types: Seq[String] = Nil
) extends ActionRequest {
  def method: String = "GET"
  def urlPath: String = this.makeUrl(indices, "_stats", metric)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    if (completionFields.nonEmpty) {
      queryArgs += "completion_fields" -> completionFields.toList.mkString(",")
    }
    if (expandWildcards.nonEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += "expand_wildcards" -> expandWildcards.mkString(",")
      }
    }
    if (fielddataFields.nonEmpty) {
      queryArgs += "fielddata_fields" -> fielddataFields.toList.mkString(",")
    }
    if (fields.nonEmpty) {
      queryArgs += "fields" -> fields.toList.mkString(",")
    }
    if (forbidClosedIndices != true) queryArgs += "forbid_closed_indices" -> forbidClosedIndices.toString
    if (groups.nonEmpty) {
      queryArgs += "groups" -> groups.toList.mkString(",")
    }
    if (includeSegmentFileSizes != false) queryArgs += "include_segment_file_sizes" -> includeSegmentFileSizes.toString
    if (includeUnloadedSegments != false) queryArgs += "include_unloaded_segments" -> includeUnloadedSegments.toString
    if (level != Level.indices) queryArgs += "level" -> level.toString
    if (types.nonEmpty) {
      queryArgs += "types" -> types.toList.mkString(",")
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object IndicesStatsRequest {
  implicit val jsonDecoder: JsonDecoder[IndicesStatsRequest] = DeriveJsonDecoder.gen[IndicesStatsRequest]
  implicit val jsonEncoder: JsonEncoder[IndicesStatsRequest] = DeriveJsonEncoder.gen[IndicesStatsRequest]
}
