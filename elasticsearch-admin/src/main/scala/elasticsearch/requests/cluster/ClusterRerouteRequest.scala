/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.cluster

import io.circe._
import io.circe.derivation.annotations._
import scala.collection.mutable

import elasticsearch.requests.ActionRequest

/*
 * Allows to manually change the allocation of individual shards in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-reroute.html
 *
 * @param body body the body of the call
 * @param dryRun Simulate the operation only and return the resulting state
 * @param explain Return an explanation of why the commands can or cannot be executed
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param metric Limit the information returned to the specified metrics. Defaults to all but metadata
 * @param retryFailed Retries allocation of shards that are blocked due to too many subsequent allocation failures
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class ClusterRerouteRequest(
  body: Option[JsonObject] = None,
  @JsonKey("dry_run") dryRun: Option[Boolean] = None,
  explain: Option[Boolean] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  metric: Seq[String] = Nil,
  @JsonKey("retry_failed") retryFailed: Option[Boolean] = None,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath = "/_cluster/reroute"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    body.foreach { v =>
      queryArgs += ("body" -> v.toString)
    }
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
