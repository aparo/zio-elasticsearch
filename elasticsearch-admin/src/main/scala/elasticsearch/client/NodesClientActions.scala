/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import elasticsearch.ZioResponse
import elasticsearch.requests.nodes._
import elasticsearch.responses.nodes._

trait NodesClientActions {
  def execute(request: NodesHotThreadsRequest): ZioResponse[NodesHotThreadsResponse]
  def execute(request: NodesInfoRequest): ZioResponse[NodesInfoResponse]
  def execute(request: NodesReloadSecureSettingsRequest): ZioResponse[NodesReloadSecureSettingsResponse]
  def execute(request: NodesStatsRequest): ZioResponse[NodesStatsResponse]
  def execute(request: NodesUsageRequest): ZioResponse[NodesUsageResponse]

}
