/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.indices
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-shrink-index.html
 *
 * @param index The name of the source index to shrink
 * @param target The name of the target index to shrink into
 * @param body body the body of the call
 * @param timeout Explicit operation timeout
 * @param masterTimeout Specify timeout for connection to master
 * @param waitForActiveShards Set the number of active shards to wait for on the shrunken index before the operation returns.
 */
@JsonCodec
final case class ShrinkRequest(
  index: String,
  target: String,
  body: Json,
  timeout: Option[String] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  @JsonKey("wait_for_active_shards") waitForActiveShards: Option[String] = None
) extends ActionRequest {
  def method: String = "PUT"

  def urlPath: String = this.makeUrl(index, "_shrink", target)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    masterTimeout.map { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
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
