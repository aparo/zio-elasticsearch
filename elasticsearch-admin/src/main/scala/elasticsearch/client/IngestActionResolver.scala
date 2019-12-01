package elasticsearch.client

import elasticsearch.ZioResponse
import elasticsearch.requests.ingest._
import elasticsearch.responses.ingest._

trait IngestActionResolver extends IngestClientActions with ClientActionResolver {

  def execute(
               request: IngestDeletePipelineRequest
             ): ZioResponse[IngestDeletePipelineResponse] =
    doCall(request).flatMap(convertResponse[IngestDeletePipelineResponse](request))

  def execute(
               request: IngestGetPipelineRequest
             ): ZioResponse[IngestGetPipelineResponse] =
    doCall(request).flatMap(convertResponse[IngestGetPipelineResponse](request))

  def execute(
               request: IngestProcessorGrokRequest
             ): ZioResponse[IngestProcessorGrokResponse] =
    doCall(request).flatMap(convertResponse[IngestProcessorGrokResponse](request))

  def execute(
               request: IngestPutPipelineRequest
             ): ZioResponse[IngestPutPipelineResponse] =
    doCall(request).flatMap(convertResponse[IngestPutPipelineResponse](request))

  def execute(
               request: IngestSimulateRequest
             ): ZioResponse[IngestSimulateResponse] =
    doCall(request).flatMap(convertResponse[IngestSimulateResponse](request))

}
