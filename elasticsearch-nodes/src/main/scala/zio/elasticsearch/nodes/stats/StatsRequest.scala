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

package zio.elasticsearch.nodes.stats
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Returns statistical information about nodes in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-stats.html
 *
 * @param masterTimeout Period to wait for a connection to the master node. If no response is received before the timeout expires, the request fails and returns an error.
 * @server_default 30s

 * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
 * when they occur.
 * @server_default false

 * @param filterPath Comma-separated list of filters in dot notation which reduce the response
 * returned by Elasticsearch.

 * @param human When set to `true` will return statistics in a format suitable for humans.
 * For example `"exists_time": "1h"` for humans and
 * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
 * readable values will be omitted. This makes sense for responses being consumed
 * only by machines.
 * @server_default false

 * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
 * this option for debugging only.
 * @server_default false

 * @param completionFields A comma-separated list of fields for the `completion` index metric (supports wildcards)
 * @param fielddataFields A comma-separated list of fields for the `fielddata` index metric (supports wildcards)
 * @param fields A comma-separated list of fields for `fielddata` and `completion` index metric (supports wildcards)
 * @param groups A comma-separated list of search groups for `search` index metric
 * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
 * @param includeUnloadedSegments If set to true segment stats will include stats for segments that are not currently loaded into memory
 * @param indexMetric Limit the information returned for `indices` metric to the specific index metrics. Isn't used if `indices` (or `all`) metric isn't specified.
 * @param level Return indices stats aggregated at index, node or shard level
 * @param metric Limit the information returned to the specified metrics
 * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param timeout Explicit operation timeout
 * @param types A comma-separated list of document types for the `indexing` index metric
 */

final case class StatsRequest(
  masterTimeout: Option[String] = None,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  completionFields: Seq[String] = Nil,
  fielddataFields: Seq[String] = Nil,
  fields: Seq[String] = Nil,
  groups: Seq[String] = Nil,
  includeSegmentFileSizes: Boolean = false,
  includeUnloadedSegments: Boolean = false,
  indexMetric: Option[String] = None,
  level: Level = Level.node,
  metric: Option[String] = None,
  nodeId: Seq[String] = Nil,
  timeout: Option[String] = None,
  types: Seq[String] = Nil
) extends ActionRequest[Json]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String =
    this.makeUrl("_nodes", nodeId, "stats", metric, indexMetric)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (completionFields.nonEmpty) {
      queryArgs += ("completion_fields" -> completionFields.toList.mkString(
        ","
      ))
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
    if (includeUnloadedSegments != false)
      queryArgs += ("include_unloaded_segments" -> includeUnloadedSegments.toString)
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
