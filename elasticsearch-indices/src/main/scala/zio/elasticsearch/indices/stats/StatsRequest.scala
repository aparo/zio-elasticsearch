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

package zio.elasticsearch.indices.stats
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Provides statistics on operations happening in an index.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-stats.html
 *
 * @param index

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
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param fielddataFields A comma-separated list of fields for the `fielddata` index metric (supports wildcards)
 * @param fields A comma-separated list of fields for `fielddata` and `completion` index metric (supports wildcards)
 * @param forbidClosedIndices If set to false stats will also collected from closed indices if explicitly specified or if expand_wildcards expands to closed indices
 * @param groups A comma-separated list of search groups for `search` index metric
 * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
 * @param includeUnloadedSegments If set to true segment stats will include stats for segments that are not currently loaded into memory
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param level Return stats aggregated at cluster, index or shard level
 * @param metric Limit the information returned the specific metrics.
 */

final case class StatsRequest(
  index: Chunk[String],
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  completionFields: Seq[String] = Nil,
  expandWildcards: Seq[ExpandWildcards] = Nil,
  fielddataFields: Seq[String] = Nil,
  fields: Seq[String] = Nil,
  forbidClosedIndices: Boolean = true,
  groups: Seq[String] = Nil,
  includeSegmentFileSizes: Boolean = false,
  includeUnloadedSegments: Boolean = false,
  indices: Seq[String] = Nil,
  level: Level = Level.indices,
  metric: Option[String] = None
) extends ActionRequest[Json]
    with RequestBase {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, "_stats", metric)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (completionFields.nonEmpty) {
      queryArgs += ("completion_fields" -> completionFields.toList.mkString(
        ","
      ))
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
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
