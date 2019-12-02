/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import elasticsearch.ZioResponse
import elasticsearch.requests.tasks._
import elasticsearch.responses.tasks._

trait TasksClientActions {
  def execute(request: TasksCancelRequest): ZioResponse[TasksCancelResponse]
  def execute(request: TasksGetRequest): ZioResponse[TasksGetResponse]
  def execute(request: TasksListRequest): ZioResponse[TasksListResponse]

}
