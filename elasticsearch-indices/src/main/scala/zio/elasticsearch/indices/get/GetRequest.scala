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

package zio.elasticsearch.indices.get
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.indices.Feature
import zio.json.ast._
/*
 * Returns information about one or more indices.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-index.html
 *
 * @param indices A comma-separated list of index names
 * @param name

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

 * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
 * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
 * @param features Return only information on specified index features
 * @param flatSettings Return settings in flat format (default: false)
 * @param ignoreUnavailable Ignore unavailable indexes (default: false)
 * @param includeDefaults Whether to return all default setting for each of the indices.
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param masterTimeout Specify timeout for connection to master
 */

final case class GetRequest(
  indices: Seq[String] = Nil,
  name: DataStreamNames,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  allowNoIndices: Option[Boolean] = None,
  expandWildcards: Seq[ExpandWildcards] = Nil,
  features: Seq[Feature] = Nil,
  flatSettings: Option[Boolean] = None,
  ignoreUnavailable: Option[Boolean] = None,
  includeDefaults: Boolean = false,
  local: Option[Boolean] = None,
  masterTimeout: Option[String] = None
) extends ActionRequest[Json]
    with RequestBase {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    allowNoIndices.foreach { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    if (expandWildcards.nonEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    if (features.nonEmpty) {
      if (features.toSet != Set(Feature.aliases, Feature.mappings, Feature.settings)) {
        queryArgs += ("features" -> features.mkString(","))
      }

    }
    flatSettings.foreach { v =>
      queryArgs += ("flat_settings" -> v.toString)
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    if (includeDefaults != false)
      queryArgs += ("include_defaults" -> includeDefaults.toString)
    local.foreach { v =>
      queryArgs += ("local" -> v.toString)
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
