/*
 * Copyright 2019-2020 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package elasticsearch

import elasticsearch.ElasticSearchService.ElasticSearchService
import elasticsearch.client.IngestActionResolver
import elasticsearch.requests.ingest._
import elasticsearch.responses.ingest._
import io.circe._
import zio.logging.Logging
import zio._
import zio.exception.FrameworkException

object IngestService {
  type IngestService = Has[Service]

  trait Service extends IngestActionResolver {
    def loggingService: Logging.Service

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
    ): ZioResponse[IngestDeletePipelineResponse] = execute(request)

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
    ): ZioResponse[IngestGetPipelineResponse] = execute(request)

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
    ): ZioResponse[IngestProcessorGrokResponse] = execute(request)

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
    ): ZioResponse[IngestPutPipelineResponse] = execute(request)

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
    ): ZioResponse[IngestSimulateResponse] = execute(request)

  }

  // services

  private case class Live(
      loggingService: Logging.Service,
      baseElasticSearchService: ElasticSearchService.Service,
      httpService: HTTPService.Service
  ) extends Service

  val live: ZLayer[ElasticSearchService, Nothing, Has[Service]] =
    ZLayer.fromService[ElasticSearchService.Service, Service] {
      (baseElasticSearchService) =>
        Live(baseElasticSearchService.loggingService,
             baseElasticSearchService,
             baseElasticSearchService.httpService)
    }

  // access methods

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
  ): ZIO[IngestService, FrameworkException, IngestDeletePipelineResponse] =
    ZIO.accessM[IngestService](
      _.get.deletePipeline(id = id,
                           masterTimeout = masterTimeout,
                           timeout = timeout))

  def deletePipeline(
      request: IngestDeletePipelineRequest
  ): ZIO[IngestService, FrameworkException, IngestDeletePipelineResponse] =
    ZIO.accessM[IngestService](_.get.execute(request))

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
  ): ZIO[IngestService, FrameworkException, IngestGetPipelineResponse] =
    ZIO.accessM[IngestService](
      _.get.getPipeline(id = id, masterTimeout = masterTimeout))

  def getPipeline(
      request: IngestGetPipelineRequest
  ): ZIO[IngestService, FrameworkException, IngestGetPipelineResponse] =
    ZIO.accessM[IngestService](_.get.execute(request))

  /*
   * Returns a list of the built-in patterns.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/grok-processor.html#grok-processor-rest-get
   *

   */
  def processorGrok()
    : ZIO[IngestService, FrameworkException, IngestProcessorGrokResponse] =
    ZIO.accessM[IngestService](_.get.processorGrok())

  def processorGrok(
      request: IngestProcessorGrokRequest
  ): ZIO[IngestService, FrameworkException, IngestProcessorGrokResponse] =
    ZIO.accessM[IngestService](_.get.execute(request))

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
  ): ZIO[IngestService, FrameworkException, IngestPutPipelineResponse] =
    ZIO.accessM[IngestService](
      _.get.putPipeline(id = id,
                        body = body,
                        masterTimeout = masterTimeout,
                        timeout = timeout)
    )

  def putPipeline(
      request: IngestPutPipelineRequest
  ): ZIO[IngestService, FrameworkException, IngestPutPipelineResponse] =
    ZIO.accessM[IngestService](_.get.execute(request))

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
  ): ZIO[IngestService, FrameworkException, IngestSimulateResponse] =
    ZIO.accessM[IngestService](
      _.get.simulate(body = body, id = id, verbose = verbose))

  def simulate(request: IngestSimulateRequest)
    : ZIO[IngestService, FrameworkException, IngestSimulateResponse] =
    ZIO.accessM[IngestService](_.get.execute(request))

}
