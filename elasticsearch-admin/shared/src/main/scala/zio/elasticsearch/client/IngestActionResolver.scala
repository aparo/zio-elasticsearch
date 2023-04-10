/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.elasticsearch.client

import zio.elasticsearch.ZioResponse
import zio.elasticsearch.requests.ingest._
import zio.elasticsearch.responses.ingest._

trait IngestActionResolver extends IngestClientActions with ClientActionResolver {

  def execute(
    request: IngestDeletePipelineRequest
  ): ZIO[Any, FrameworkException, IngestDeletePipelineResponse] =
    doCall(request).flatMap(convertResponse[IngestDeletePipelineResponse](request))

  def execute(
    request: IngestGetPipelineRequest
  ): ZIO[Any, FrameworkException, IngestGetPipelineResponse] =
    doCall(request).flatMap(convertResponse[IngestGetPipelineResponse](request))

  def execute(
    request: IngestProcessorGrokRequest
  ): ZIO[Any, FrameworkException, IngestProcessorGrokResponse] =
    doCall(request).flatMap(convertResponse[IngestProcessorGrokResponse](request))

  def execute(
    request: IngestPutPipelineRequest
  ): ZIO[Any, FrameworkException, IngestPutPipelineResponse] =
    doCall(request).flatMap(convertResponse[IngestPutPipelineResponse](request))

  def execute(
    request: IngestSimulateRequest
  ): ZIO[Any, FrameworkException, IngestSimulateResponse] =
    doCall(request).flatMap(convertResponse[IngestSimulateResponse](request))

}
