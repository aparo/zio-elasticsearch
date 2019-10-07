/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable

/*
 * https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-reindex.html
 *
 * @param body body the body of the call
 * @param refresh Should the effected indexes be refreshed?
 * @param waitForCompletion Should the request should block until the reindex is complete.
 * @param slices The number of slices this task should be divided into. Defaults to 1 meaning the task isn't sliced into subtasks.
 * @param requestsPerSecond The throttle to set on this request in sub-requests per second. -1 means no throttle.
 * @param timeout Time each individual bulk request should wait for shards that are unavailable.
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the reindex operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */
@JsonCodec
final case class ReindexRequest(
  body: Json,
  refresh: Option[Boolean] = None,
  @JsonKey("wait_for_completion") waitForCompletion: Boolean = false,
  slices: Double = 1,
  @JsonKey("requests_per_second") requestsPerSecond: Double = 0,
  timeout: String = "1m",
  @JsonKey("wait_for_active_shards") waitForActiveShards: Option[String] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = "/_reindex"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    refresh.map { v =>
      queryArgs += ("refresh" -> v.toString)
    }
    if (waitForCompletion != false)
      queryArgs += ("wait_for_completion" -> waitForCompletion.toString)
    if (slices != 1) queryArgs += ("slices" -> slices.toString)
    if (requestsPerSecond != 0)
      queryArgs += ("requests_per_second" -> requestsPerSecond.toString)
    if (timeout != "1m") queryArgs += ("timeout" -> timeout.toString)
    waitForActiveShards.map { v =>
      queryArgs += ("wait_for_active_shards" -> v)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
