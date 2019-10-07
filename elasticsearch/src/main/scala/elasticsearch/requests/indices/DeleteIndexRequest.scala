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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-delete-index.html
 *
 * @param indices A list of indices to delete; use `_all` or `*` string to delete all indices
 * @param timeout Explicit operation timeout
 * @param masterTimeout Specify timeout for connection to master
 */
@JsonCodec
final case class DeleteIndexRequest(
  indices: Seq[String] = Nil,
  timeout: Option[String] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None
) extends ActionRequest {
  def method: String = "DELETE"

  def urlPath: String = this.makeUrl(indices)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    masterTimeout.map { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
