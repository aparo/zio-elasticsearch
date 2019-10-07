/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.nodes
import io.circe._
import scala.collection.mutable
import elasticsearch.requests.ActionRequest
import elasticsearch.Type
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-hot-threads.html
 *
 * @param nodeId A list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param snapshots Number of samples of thread stacktrace (default: 10)
 * @param interval The interval for the second sampling of threads
 * @param `type` The type to sample (default: cpu)
 * @param ignoreIdleThreads Don't show threads that are in known-idle places, such as waiting on a socket select or pulling from an empty task queue (default: true)
 * @param threads Specify the number of threads to provide information for (default: 3)
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class NodesHotThreadsRequest(
  @JsonKey("node_id") nodeId: Seq[String] = Nil,
  snapshots: Double = 10,
  interval: Option[String] = None,
  @JsonKey("type") `type`: Type = Type.Cpu,
  @JsonKey("ignore_idle_threads") ignoreIdleThreads: Boolean = true,
  threads: Double = 3,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_cluster", "nodes", nodeId, "hot_threads")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (snapshots != 10) queryArgs += ("snapshots" -> snapshots.toString)
    interval.map { v =>
      queryArgs += ("interval" -> v.toString)
    }
    if (`type` != Type.Cpu)
      queryArgs += ("type" -> `type`.toString)
    if (!ignoreIdleThreads)
      queryArgs += ("ignore_idle_threads" -> ignoreIdleThreads.toString)
    if (threads != 3) queryArgs += ("threads" -> threads.toString)
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
