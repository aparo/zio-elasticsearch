/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.nodes

import elasticsearch.Level
import io.circe._
import io.circe.derivation.annotations._

import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * Returns statistical information about nodes in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-stats.html
 *
 * @param completionFields A comma-separated list of fields for `fielddata` and `suggest` index metric (supports wildcards)
 * @param fielddataFields A comma-separated list of fields for `fielddata` index metric (supports wildcards)
 * @param fields A comma-separated list of fields for `fielddata` and `completion` index metric (supports wildcards)
 * @param groups A comma-separated list of search groups for `search` index metric
 * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
 * @param indexMetric Limit the information returned for `indices` metric to the specific index metrics. Isn't used if `indices` (or `all`) metric isn't specified.
 * @param level Return indices stats aggregated at index, node or shard level
 * @param metric Limit the information returned to the specified metrics
 * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param timeout Explicit operation timeout
 * @param types A comma-separated list of document types for the `indexing` index metric
 */
@JsonCodec
final case class NodesStatsRequest(
  @JsonKey("completion_fields") completionFields: Seq[String] = Nil,
  @JsonKey("fielddata_fields") fielddataFields: Seq[String] = Nil,
  fields: Seq[String] = Nil,
  groups: Seq[String] = Nil,
  @JsonKey("include_segment_file_sizes") includeSegmentFileSizes: Boolean = false,
  @JsonKey("index_metric") indexMetric: Option[String] = None,
  level: Level = Level.node,
  metric: Option[String] = None,
  @JsonKey("node_id") nodeId: Seq[String] = Nil,
  timeout: Option[String] = None,
  types: Seq[String] = Nil
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_nodes", nodeId, "stats", metric, indexMetric)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (completionFields.nonEmpty) {
      queryArgs += ("completion_fields" -> completionFields.toList.mkString(","))
    }
    if (fielddataFields.nonEmpty) {
      queryArgs += ("fielddata_fields" -> fielddataFields.toList.mkString(","))
    }
    if (fields.nonEmpty) {
      queryArgs += ("fields" -> fields.toList.mkString(","))
    }
    if (groups.nonEmpty) {
      queryArgs += ("groups" -> groups.toList.mkString(","))
    }
    if (includeSegmentFileSizes != false)
      queryArgs += ("include_segment_file_sizes" -> includeSegmentFileSizes.toString)
    if (level != Level.node)
      queryArgs += ("level" -> level.toString)
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
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
