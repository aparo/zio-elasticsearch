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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-create-index.html
 *
 * @param index The name of the index
 * @param body body the body of the call
 * @param waitForActiveShards Set the number of active shards to wait for before the operation returns.
 * @param timeout Explicit operation timeout
 * @param masterTimeout Specify timeout for connection to master
 * @param updateAllTypes Whether to update the mapping for all fields with the same name across all types or not
 */
@JsonCodec
final case class CreateIndexRequest(
  index: String,
  body: JsonObject,
  @JsonKey("wait_for_active_shards") waitForActiveShards: Option[Int] = None,
  timeout: Option[String] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  @JsonKey("update_all_types") updateAllTypes: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "PUT"

  def urlPath: String = this.makeUrl(index)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    waitForActiveShards.map { v =>
      queryArgs += ("wait_for_active_shards" -> v.toString)
    }
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    masterTimeout.map { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    updateAllTypes.map { v =>
      queryArgs += ("update_all_types" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
