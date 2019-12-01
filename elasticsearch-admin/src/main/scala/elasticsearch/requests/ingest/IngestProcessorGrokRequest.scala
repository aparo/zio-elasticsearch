/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.ingest

import io.circe._
import io.circe.derivation.annotations._

import elasticsearch.requests.ActionRequest

/*
 * Returns a list of the built-in patterns.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/grok-processor.html#grok-processor-rest-get
 *

 */
@JsonCodec
final case class IngestProcessorGrokRequest(
    )
    extends ActionRequest {
  def method: String = "GET"

  def urlPath = "/_ingest/processor/grok"

  def queryArgs: Map[String, String] = Map.empty[String, String]

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
