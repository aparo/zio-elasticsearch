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

package zio.elasticsearch.transform.get_transform_stats
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Retrieves usage information for transforms.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-transform-stats.html
 *
 * @param transformId The id of the transform for which to get stats. '_all' or '*' implies all transforms
 * @param timeout

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

 * @param allowNoMatch Whether to ignore if a wildcard expression matches no transforms. (This includes `_all` string or when no transforms have been specified)
 * @param from skips a number of transform stats, defaults to 0
 * @param size specifies a max number of transform stats to get, defaults to 100
 */

final case class GetTransformStatsRequest(
  transformId: String,
  timeout: Option[String] = None,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  allowNoMatch: Option[Boolean] = None,
  from: Option[Double] = None,
  size: Option[Double] = None
) extends ActionRequest[Json]
    with RequestBase {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_transform", transformId, "_stats")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    allowNoMatch.foreach { v =>
      queryArgs += ("allow_no_match" -> v.toString)
    }
    from.foreach { v =>
      queryArgs += ("from" -> v.toString)
    }
    size.foreach { v =>
      queryArgs += ("size" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
