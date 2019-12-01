/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests
import io.circe._
import io.circe.derivation.annotations.{JsonCodec, JsonKey}

import scala.collection.mutable

/*
 * Changes the number of requests per second for a particular Reindex operation.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-reindex.html
 *
 * @param requestsPerSecond The throttle to set on this request in floating sub-requests per second. -1 means set no throttle.
 * @param taskId The task id to rethrottle
 */
@JsonCodec
final case class ReindexRethrottleRequest(
    @JsonKey("requests_per_second") requestsPerSecond: Int,
    @JsonKey("task_id") taskId: String
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl("_reindex", taskId, "_rethrottle")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    queryArgs += ("requests_per_second" -> requestsPerSecond.toString)
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
