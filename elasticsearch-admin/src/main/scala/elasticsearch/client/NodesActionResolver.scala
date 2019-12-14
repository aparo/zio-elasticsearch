/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

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
