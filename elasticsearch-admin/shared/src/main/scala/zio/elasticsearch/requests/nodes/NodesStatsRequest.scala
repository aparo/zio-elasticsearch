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

package zio.elasticsearch.requests.nodes

import zio.elasticsearch.common.Level
import scala.collection.mutable
import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

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
final case class NodesStatsRequest(
  @jsonField("completion_fields") completionFields: Chunk[String] = Chunk.empty,
  @jsonField("fielddata_fields") fielddataFields: Chunk[String] = Chunk.empty,
  fields: Chunk[String] = Chunk.empty,
  groups: Chunk[String] = Chunk.empty,
  @jsonField("include_segment_file_sizes") includeSegmentFileSizes: Boolean = false,
  @jsonField("index_metric") indexMetric: Option[String] = None,
  level: Level = Level.node,
  metric: Option[String] = None,
  @jsonField("node_id") nodeId: Chunk[String] = Chunk.empty,
  timeout: Option[String] = None,
  types: Chunk[String] = Chunk.empty
) extends ActionRequest {
  def method: Method = Method.GET
  def urlPath: String = this.makeUrl("_nodes", nodeId, "stats", metric, indexMetric)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    if (completionFields.nonEmpty) {
      queryArgs += "completion_fields" -> completionFields.toList.mkString(",")
    }
    if (fielddataFields.nonEmpty) {
      queryArgs += "fielddata_fields" -> fielddataFields.toList.mkString(",")
    }
    if (fields.nonEmpty) {
      queryArgs += "fields" -> fields.toList.mkString(",")
    }
    if (groups.nonEmpty) {
      queryArgs += "groups" -> groups.toList.mkString(",")
    }
    if (includeSegmentFileSizes != false) queryArgs += "include_segment_file_sizes" -> includeSegmentFileSizes.toString
    if (level != Level.node) queryArgs += "level" -> level.toString
    timeout.foreach { v =>
      queryArgs += "timeout" -> v.toString
    }
    if (types.nonEmpty) {
      queryArgs += "types" -> types.toList.mkString(",")
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object NodesStatsRequest {
  implicit val jsonDecoder: JsonDecoder[NodesStatsRequest] = DeriveJsonDecoder.gen[NodesStatsRequest]
  implicit val jsonEncoder: JsonEncoder[NodesStatsRequest] = DeriveJsonEncoder.gen[NodesStatsRequest]
}
