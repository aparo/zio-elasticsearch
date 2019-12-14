/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.indices

import elasticsearch.{ ExpandWildcards, Level }
import io.circe._
import io.circe.derivation.annotations._

import scala.collection.mutable
import elasticsearch.requests.ActionRequest

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
@JsonCodec
final case class IndicesStatsRequest(
  @JsonKey("completion_fields") completionFields: Seq[String] = Nil,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @JsonKey("fielddata_fields") fielddataFields: Seq[String] = Nil,
  fields: Seq[String] = Nil,
  @JsonKey("forbid_closed_indices") forbidClosedIndices: Boolean = true,
  groups: Seq[String] = Nil,
  @JsonKey("include_segment_file_sizes") includeSegmentFileSizes: Boolean = false,
  @JsonKey("include_unloaded_segments") includeUnloadedSegments: Boolean = false,
  indices: Seq[String] = Nil,
  level: Level = Level.indices,
  metric: Option[String] = None,
  types: Seq[String] = Nil
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, "_stats", metric)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (completionFields.nonEmpty) {
      queryArgs += ("completion_fields" -> completionFields.toList.mkString(","))
    }
    if (expandWildcards.nonEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    if (fielddataFields.nonEmpty) {
      queryArgs += ("fielddata_fields" -> fielddataFields.toList.mkString(","))
    }
    if (fields.nonEmpty) {
      queryArgs += ("fields" -> fields.toList.mkString(","))
    }
    if (forbidClosedIndices != true)
      queryArgs += ("forbid_closed_indices" -> forbidClosedIndices.toString)
    if (groups.nonEmpty) {
      queryArgs += ("groups" -> groups.toList.mkString(","))
    }
    if (includeSegmentFileSizes != false)
      queryArgs += ("include_segment_file_sizes" -> includeSegmentFileSizes.toString)
    if (includeUnloadedSegments != false)
      queryArgs += ("include_unloaded_segments" -> includeUnloadedSegments.toString)
    if (level != Level.indices)
      queryArgs += ("level" -> level.toString)
    if (types.nonEmpty) {
      queryArgs += ("types" -> types.toList.mkString(","))
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
