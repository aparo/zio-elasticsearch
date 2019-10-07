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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-rollover-index.html
 *
 * @param alias The name of the alias to rollover
 * @param newIndex The name of the rollover index
 * @param body body the body of the call
 * @param timeout Explicit operation timeout
 * @param dryRun If set to true the rollover action will only be validated but not actually performed even if a condition matches. The default is false
 * @param masterTimeout Specify timeout for connection to master
 * @param waitForActiveShards Set the number of active shards to wait for on the newly created rollover index before the operation returns.
 */
@JsonCodec
final case class RolloverRequest(
  alias: String,
  @JsonKey("new_index") newIndex: Option[String] = None,
  body: Json,
  timeout: Option[String] = None,
  @JsonKey("dry_run") dryRun: Option[Boolean] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  @JsonKey("wait_for_active_shards") waitForActiveShards: Option[String] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(alias, "_rollover", newIndex)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    dryRun.map { v =>
      queryArgs += ("dry_run" -> v.toString)
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
