/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.cluster
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import elasticsearch.requests.ActionRequest
import elasticsearch.Level
import elasticsearch.WaitForStatus
import elasticsearch.WaitForEvents

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-health.html
 *
 * @param index Limit the information returned to a specific index
 * @param waitForNodes Wait until the specified number of nodes is available
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param waitForNoRelocatingShards Whether to wait until there are no relocating shards in the cluster
 * @param waitForStatus Wait until cluster is in a specific state
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param timeout Explicit operation timeout
 * @param waitForEvents Wait until all currently queued events with the given priority are processed
 * @param waitForActiveShards Wait until the specified number of shards is active
 * @param level Specify the level of detail for returned information
 */
@JsonCodec
final case class ClusterHealthRequest(
  indices: Seq[String] = Nil,
  @JsonKey("wait_for_nodes") waitForNodes: Option[String] = None,
  local: Boolean = false,
  @JsonKey("wait_for_no_relocating_shards") waitForNoRelocatingShards: Option[
    Int
  ] = None,
  @JsonKey("wait_for_status") waitForStatus: Option[WaitForStatus] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None,
  @JsonKey("wait_for_events") waitForEvents: Option[WaitForEvents] = None,
  @JsonKey("wait_for_active_shards") waitForActiveShards: Option[Int] = None,
  level: Level = Level.cluster
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_cluster", "health", indices)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    waitForNodes.map { v =>
      queryArgs += ("wait_for_nodes" -> v)
    }
    if (local) queryArgs += ("local" -> local.toString)
    waitForNoRelocatingShards.map { v =>
      queryArgs += ("wait_for_no_relocating_shards" -> v.toString)
    }
    waitForStatus.map { v =>
      queryArgs += ("wait_for_status" -> v.toString)
    }
    masterTimeout.map { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    if (!waitForEvents.isEmpty) {
      queryArgs += ("wait_for_events" -> waitForEvents.toString)
//        match {
//           case Some(e) => e.mkString(",")
//           case e => e.mkString(","))
//        })

    }
    waitForActiveShards.map { v =>
      queryArgs += ("wait_for_active_shards" -> v.toString)
    }
    if (level != Level.cluster)
      queryArgs += ("level" -> level.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
