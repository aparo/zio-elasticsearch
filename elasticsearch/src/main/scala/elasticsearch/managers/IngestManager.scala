/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.managers

import elasticsearch.ElasticSearch
import elasticsearch.requests.ingest._
import elasticsearch.responses.ingest._
import io.circe._
import elasticsearch.ZioResponse

class IngestManager(client: ElasticSearch) {

  /*
   * https://www.elastic.co/guide/en/elasticsearch/plugins/master/ingest.html
   *
   * @param id Comma separated list of pipeline ids. Wildcards supported
   * @param masterTimeout Explicit operation timeout for connection to master node
   */
  def getPipeline(
    id: String,
    masterTimeout: Option[String] = None
  ): ZioResponse[GetPipelineResponse] = {
    val request = GetPipelineRequest(id = id, masterTimeout = masterTimeout)

    getPipeline(request)

  }

  def getPipeline(
    request: GetPipelineRequest
  ): ZioResponse[GetPipelineResponse] =
    client.execute(request)

  /*
   * https://www.elastic.co/guide/en/elasticsearch/plugins/master/ingest.html
   *
   * @param body body the body of the call
   * @param id Pipeline ID
   * @param verbose Verbose mode. Display data output for each processor in executed pipeline
   */
  def simulate(
    body: Json,
    id: Option[String] = None,
    verbose: Boolean = false
  ): ZioResponse[SimulatePipelineResponse] = {
    val request =
      SimulatePipelineRequest(body = body, id = id, verbose = verbose)

    simulate(request)

  }

  def simulate(
    request: SimulatePipelineRequest
  ): ZioResponse[SimulatePipelineResponse] =
    client.execute(request)

}
