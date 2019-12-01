/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.snapshot

import io.circe._
import io.circe.derivation.annotations._
import scala.collection.mutable

import elasticsearch.requests.ActionRequest

/*
 * Creates a snapshot in a repository.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
 *
 * @param repository A repository name
 * @param snapshot A snapshot name
 * @param body body the body of the call
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param waitForCompletion Should this request wait until the operation has completed before returning
 */
@JsonCodec
final case class SnapshotCreateRequest(
    repository: String,
    snapshot: String,
    body: Option[JsonObject] = None,
    @JsonKey("master_timeout") masterTimeout: Option[String] = None,
    @JsonKey("wait_for_completion") waitForCompletion: Boolean = false
) extends ActionRequest {
  def method: String = "PUT"

  def urlPath: String = this.makeUrl("_snapshot", repository, snapshot)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    body.foreach { v =>
      queryArgs += ("body" -> v.toString)
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    if (waitForCompletion != false)
      queryArgs += ("wait_for_completion" -> waitForCompletion.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
