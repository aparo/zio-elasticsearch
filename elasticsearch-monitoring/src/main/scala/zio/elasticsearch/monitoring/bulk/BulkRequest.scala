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

package zio.elasticsearch.monitoring.bulk
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
/*
 * Used by the monitoring features to send monitoring data.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/monitor-elasticsearch-cluster.html
 *
 * @param `type`
@deprecated 7.0.0

 * @param body body the body of the call
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

 * @param interval Collection interval (e.g., '10s' or '10000ms') of the payload
 * @param systemApiVersion API Version of the monitored system
 * @param systemId Identifier of the monitored system
 */

final case class BulkRequest(
  `type`: String,
  body: Array[String],
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  interval: Option[String] = None,
  systemApiVersion: Option[String] = None,
  systemId: Option[String] = None
) extends ActionRequest[Array[String]]
    with RequestBase {
  def method: Method = Method.POST

  def urlPath = "/_monitoring/bulk"

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    interval.foreach { v =>
      queryArgs += ("interval" -> v)
    }
    systemApiVersion.foreach { v =>
      queryArgs += ("system_api_version" -> v)
    }
    systemId.foreach { v =>
      queryArgs += ("system_id" -> v)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
