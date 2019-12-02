/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import elasticsearch.ZioResponse
import elasticsearch.requests.ingest._
import elasticsearch.responses.ingest._

trait IngestClientActions {
  def execute(request: IngestDeletePipelineRequest): ZioResponse[IngestDeletePipelineResponse]
  def execute(request: IngestGetPipelineRequest): ZioResponse[IngestGetPipelineResponse]
  def execute(request: IngestProcessorGrokRequest): ZioResponse[IngestProcessorGrokResponse]
  def execute(request: IngestPutPipelineRequest): ZioResponse[IngestPutPipelineResponse]
  def execute(request: IngestSimulateRequest): ZioResponse[IngestSimulateResponse]

}
