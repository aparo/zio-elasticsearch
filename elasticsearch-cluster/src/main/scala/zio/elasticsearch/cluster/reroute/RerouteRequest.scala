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

package zio.elasticsearch.cluster.reroute
import scala.collection.mutable
import zio._
import zio.elasticsearch.cluster.requests.RerouteRequestBody
import zio.elasticsearch.common._
/*
 * Allows to manually change the allocation of individual shards in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-reroute.html
 *
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

 * @param body body the body of the call
 * @param dryRun Simulate the operation only and return the resulting state
 * @param explain Return an explanation of why the commands can or cannot be executed
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param metric Limit the information returned to the specified metrics. Defaults to all but metadata
 * @param retryFailed Retries allocation of shards that are blocked due to too many subsequent allocation failures
 * @param timeout Explicit operation timeout
 */

final case class RerouteRequest(
  body: RerouteRequestBody = RerouteRequestBody(),
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  dryRun: Option[Boolean] = None,
  explain: Option[Boolean] = None,
  masterTimeout: Option[String] = None,
  metric: Seq[String] = Nil,
  retryFailed: Option[Boolean] = None,
  timeout: Option[String] = None
) extends ActionRequest[RerouteRequestBody]
    with RequestBase {
  def method: Method = Method.POST

  def urlPath = "/_cluster/reroute"

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    dryRun.foreach { v =>
      queryArgs += ("dry_run" -> v.toString)
    }
    explain.foreach { v =>
      queryArgs += ("explain" -> v.toString)
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    if (metric.nonEmpty) {
      queryArgs += ("metric" -> metric.toList.mkString(","))
    }
    retryFailed.foreach { v =>
      queryArgs += ("retry_failed" -> v.toString)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
