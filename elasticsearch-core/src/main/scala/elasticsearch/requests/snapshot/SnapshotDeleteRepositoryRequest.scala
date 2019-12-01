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
 * Deletes a repository.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
 *
 * @param repository A comma-separated list of repository names
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class SnapshotDeleteRepositoryRequest(
  repository: Seq[String] = Nil,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "DELETE"

  def urlPath: String = this.makeUrl("_snapshot", repository)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
