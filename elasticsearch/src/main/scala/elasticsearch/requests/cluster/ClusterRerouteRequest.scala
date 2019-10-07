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

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-reroute.html
 *
 * @param body body the body of the call
 * @param explain Return an explanation of why the commands can or cannot be executed
 * @param dryRun Simulate the operation only and return the resulting state
 * @param metric Limit the information returned to the specified metrics. Defaults to all but metadata
 * @param retryFailed Retries allocation of shards that are blocked due to too many subsequent allocation failures
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class ClusterRerouteRequest(
  body: Json,
  explain: Option[Boolean] = None,
  @JsonKey("dry_run") dryRun: Option[Boolean] = None,
  metric: Seq[String] = Nil,
  @JsonKey("retry_failed") retryFailed: Option[Boolean] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = "/_cluster/reroute"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    explain.map { v =>
      queryArgs += ("explain" -> v.toString)
    }
    dryRun.map { v =>
      queryArgs += ("dry_run" -> v.toString)
    }
    if (!metric.isEmpty) {
      queryArgs += ("metric" -> metric.toList.mkString(","))
    }
    retryFailed.map { v =>
      queryArgs += ("retry_failed" -> v.toString)
    }
    masterTimeout.map { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
