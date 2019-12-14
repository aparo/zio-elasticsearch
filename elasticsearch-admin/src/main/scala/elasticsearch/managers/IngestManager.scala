/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.managers

import elasticsearch._
import io.circe._
import elasticsearch.ZioResponse
import elasticsearch.client.IngestActionResolver
import elasticsearch.requests.ingest._
import elasticsearch.responses.ingest._

class IngestManager(client: IngestActionResolver) {

  /*
   * Deletes a pipeline.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/delete-pipeline-api.html
   *
   * @param id Pipeline ID
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def deletePipeline(
    id: String,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZioResponse[IngestDeletePipelineResponse] = {
    val request = IngestDeletePipelineRequest(
      id = id,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    deletePipeline(request)

  }

  def deletePipeline(
    request: IngestDeletePipelineRequest
  ): ZioResponse[IngestDeletePipelineResponse] = client.execute(request)

  /*
   * Returns a pipeline.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/get-pipeline-api.html
   *
   * @param id Comma separated list of pipeline ids. Wildcards supported
   * @param masterTimeout Explicit operation timeout for connection to master node
   */
  def getPipeline(
    id: Option[String] = None,
    masterTimeout: Option[String] = None
  ): ZioResponse[IngestGetPipelineResponse] = {
    val request =
      IngestGetPipelineRequest(id = id, masterTimeout = masterTimeout)

    getPipeline(request)

  }

  def getPipeline(
    request: IngestGetPipelineRequest
  ): ZioResponse[IngestGetPipelineResponse] = client.execute(request)

  /*
   * Returns a list of the built-in patterns.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/grok-processor.html#grok-processor-rest-get
   *

   */
  def processorGrok(
    ): ZioResponse[IngestProcessorGrokResponse] = {
    val request = IngestProcessorGrokRequest()

    processorGrok(request)

  }

  def processorGrok(
    request: IngestProcessorGrokRequest
  ): ZioResponse[IngestProcessorGrokResponse] = client.execute(request)

  /*
   * Creates or updates a pipeline.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/put-pipeline-api.html
   *
   * @param id Pipeline ID
   * @param body body the body of the call
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def putPipeline(
    id: String,
    body: JsonObject,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZioResponse[IngestPutPipelineResponse] = {
    val request = IngestPutPipelineRequest(
      id = id,
      body = body,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    putPipeline(request)

  }

  def putPipeline(
    request: IngestPutPipelineRequest
  ): ZioResponse[IngestPutPipelineResponse] = client.execute(request)

  /*
   * Allows to simulate a pipeline with example documents.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/simulate-pipeline-api.html
   *
   * @param body body the body of the call
   * @param id Pipeline ID
   * @param verbose Verbose mode. Display data output for each processor in executed pipeline
   */
  def simulate(
    body: JsonObject,
    id: Option[String] = None,
    verbose: Boolean = false
  ): ZioResponse[IngestSimulateResponse] = {
    val request =
      IngestSimulateRequest(body = body, id = id, verbose = verbose)

    simulate(request)

  }

  def simulate(
    request: IngestSimulateRequest
  ): ZioResponse[IngestSimulateResponse] = client.execute(request)

}
