/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.ingest

import io.circe._
import io.circe.derivation.annotations._
import scala.collection.mutable

import elasticsearch.requests.ActionRequest

/*
 * Returns a pipeline.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/get-pipeline-api.html
 *
 * @param id Comma separated list of pipeline ids. Wildcards supported
 * @param masterTimeout Explicit operation timeout for connection to master node
 */
@JsonCodec
final case class IngestGetPipelineRequest(
  id: Option[String] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_ingest", "pipeline", id)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    masterTimeout.foreach { v =>
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
