/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import elasticsearch.ZioResponse
import elasticsearch.requests.tasks._
import elasticsearch.responses.tasks._

trait TasksActionResolver extends TasksClientActions with ClientActionResolver {
  def execute(
    request: TasksCancelRequest
  ): ZioResponse[TasksCancelResponse] =
    doCall(request).flatMap(convertResponse[TasksCancelResponse](request))

  def execute(
    request: TasksGetRequest
  ): ZioResponse[TasksGetResponse] =
    doCall(request).flatMap(convertResponse[TasksGetResponse](request))

  def execute(
    request: TasksListRequest
  ): ZioResponse[TasksListResponse] =
    doCall(request).flatMap(convertResponse[TasksListResponse](request))
}
