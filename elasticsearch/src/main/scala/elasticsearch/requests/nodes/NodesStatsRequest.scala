/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.nodes
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import elasticsearch.requests.ActionRequest
import elasticsearch.Level

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-stats.html
 *
 * @param nodeId A list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param metric Limit the information returned to the specified metrics
 * @param indexMetric Limit the information returned for `indices` metric to the specific index metrics. Isn't used if `indices` (or `all`) metric isn't specified.
 * @param fielddataFields A list of fields for `fielddata` index metric (supports wildcards)
 * @param groups A list of search groups for `search` index metric
 * @param completionFields A list of fields for `fielddata` and `suggest` index metric (supports wildcards)
 * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
 * @param fields A list of fields for `fielddata` and `completion` index metric (supports wildcards)
 * @param types A list of document types for the `indexing` index metric
 * @param timeout Explicit operation timeout
 * @param level Return indices stats aggregated at index, node or shard level
 */
@JsonCodec
final case class NodesStatsRequest(
  @JsonKey("node_id") nodeId: Seq[String] = Nil,
  metric: Option[String] = None,
  @JsonKey("index_metric") indexMetric: Option[String] = None,
  @JsonKey("fielddata_fields") fielddataFields: Seq[String] = Nil,
  groups: Seq[String] = Nil,
  @JsonKey("completion_fields") completionFields: Seq[String] = Nil,
  @JsonKey("include_segment_file_sizes") includeSegmentFileSizes: Boolean = false,
  fields: Seq[String] = Nil,
  types: Seq[String] = Nil,
  timeout: Option[String] = None,
  level: Level = Level.node
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String =
    this.makeUrl("_nodes", nodeId, "stats", metric, indexMetric)

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
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    if (level != Level.node)
      queryArgs += ("level" -> level.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
