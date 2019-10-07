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
 * @param taskId The task id to rethrottle
 * @param requestsPerSecond The throttle to set on this request in floating sub-requests per second. -1 means set no throttle.
 */
@JsonCodec
final case class ReindexRethrottleRequest(
  @JsonKey("task_id") taskId: String,
  @JsonKey("requests_per_second") requestsPerSecond: Option[Double] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl("_update_by_query", taskId, "_rethrottle")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    requestsPerSecond.map { v =>
      queryArgs += ("requests_per_second" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
