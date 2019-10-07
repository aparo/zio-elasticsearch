/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.indices
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import elasticsearch.requests.ActionRequest
import elasticsearch.Level

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-stats.html
 *
 * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
 * @param metric Limit the information returned the specific metrics.
 * @param fielddataFields A list of fields for `fielddata` index metric (supports wildcards)
 * @param groups A list of search groups for `search` index metric
 * @param completionFields A list of fields for `fielddata` and `suggest` index metric (supports wildcards)
 * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
 * @param fields A list of fields for `fielddata` and `completion` index metric (supports wildcards)
 * @param types A list of document types for the `indexing` index metric
 * @param level Return stats aggregated at cluster, index or shard level
 */
@JsonCodec
final case class IndicesStatsRequest(
  indices: Seq[String] = Nil,
  metric: Option[String] = None,
  @JsonKey("fielddata_fields") fielddataFields: Seq[String] = Nil,
  groups: Seq[String] = Nil,
  @JsonKey("completion_fields") completionFields: Seq[String] = Nil,
  @JsonKey("include_segment_file_sizes") includeSegmentFileSizes: Boolean = false,
  fields: Seq[String] = Nil,
  types: Seq[String] = Nil,
  level: Level = Level.indices
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, "_stats", metric)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!fielddataFields.isEmpty) {
      queryArgs += ("fielddata_fields" -> fielddataFields.toList.mkString(","))
    }
    if (!groups.isEmpty) {
      queryArgs += ("groups" -> groups.toList.mkString(","))
    }
    if (!completionFields.isEmpty) {
      queryArgs += ("completion_fields" -> completionFields.toList.mkString(","))
    }
    if (includeSegmentFileSizes != false)
      queryArgs += ("include_segment_file_sizes" -> includeSegmentFileSizes.toString)
    if (!fields.isEmpty) {
      queryArgs += ("fields" -> fields.toList.mkString(","))
    }
    if (!types.isEmpty) {
      queryArgs += ("types" -> types.toList.mkString(","))
    }
    if (level != Level.indices)
      queryArgs += ("level" -> level.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
