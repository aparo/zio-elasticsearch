/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.snapshot
import io.circe._
import scala.collection.mutable
import elasticsearch.requests.ActionRequest
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
 *
 * @param repository A repository name
 * @param body body the body of the call
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param timeout Explicit operation timeout
 * @param verify Whether to verify the repository after creation
 */
@JsonCodec
final case class PutRepositoryRequest(
  repository: String,
  body: Json,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None,
  verify: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "PUT"

  def urlPath: String = this.makeUrl("_snapshot", repository)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    masterTimeout.map { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    verify.map { v =>
      queryArgs += ("verify" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
