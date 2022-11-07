/*
 * Copyright 2019 Alberto Paro
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

import elasticsearch.ZioResponse
import elasticsearch.requests.nodes._
import elasticsearch.responses.nodes._

trait NodesActionResolver extends NodesClientActions with ClientActionResolver {

  def execute(
    request: NodesHotThreadsRequest
  ): ZioResponse[NodesHotThreadsResponse] =
    doCall(request).flatMap(convertResponse[NodesHotThreadsResponse](request))

  def execute(
    request: NodesInfoRequest
  ): ZioResponse[NodesInfoResponse] =
    doCall(request).flatMap(convertResponse[NodesInfoResponse](request))

  def execute(
    request: NodesReloadSecureSettingsRequest
  ): ZioResponse[NodesReloadSecureSettingsResponse] =
    doCall(request).flatMap(convertResponse[NodesReloadSecureSettingsResponse](request))

  def execute(
    request: NodesStatsRequest
  ): ZioResponse[NodesStatsResponse] =
    doCall(request).flatMap(convertResponse[NodesStatsResponse](request))

  def execute(
    request: NodesUsageRequest
  ): ZioResponse[NodesUsageResponse] =
    doCall(request).flatMap(convertResponse[NodesUsageResponse](request))

}
