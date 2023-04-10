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
import zio.elasticsearch.requests.nodes._
import zio.elasticsearch.responses.nodes._

trait NodesClientActions {
  def execute(request: NodesHotThreadsRequest): ZIO[Any, FrameworkException, NodesHotThreadsResponse]
  def execute(request: NodesInfoRequest): ZIO[Any, FrameworkException, NodesInfoResponse]
  def execute(
    request: NodesReloadSecureSettingsRequest
  ): ZIO[Any, FrameworkException, NodesReloadSecureSettingsResponse]
  def execute(request: NodesStatsRequest): ZIO[Any, FrameworkException, NodesStatsResponse]
  def execute(request: NodesUsageRequest): ZIO[Any, FrameworkException, NodesUsageResponse]

}
