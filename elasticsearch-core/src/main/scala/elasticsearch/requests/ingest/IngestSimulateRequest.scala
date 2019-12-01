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
 * Allows to simulate a pipeline with example documents.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/simulate-pipeline-api.html
 *
 * @param body body the body of the call
 * @param id Pipeline ID
 * @param verbose Verbose mode. Display data output for each processor in executed pipeline
 */
@JsonCodec
final case class IngestSimulateRequest(
  body: JsonObject,
  id: Option[String] = None,
  verbose: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_ingest", "pipeline", id, "_simulate")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (verbose != false) queryArgs += ("verbose" -> verbose.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
