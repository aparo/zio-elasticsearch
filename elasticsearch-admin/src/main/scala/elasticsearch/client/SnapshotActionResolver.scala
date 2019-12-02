/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import elasticsearch.ZioResponse
import elasticsearch.requests.snapshot._
import elasticsearch.responses.snapshot._

trait SnapshotActionResolver extends SnapshotClientActions with ClientActionResolver {

  def execute(
    request: SnapshotCleanupRepositoryRequest
  ): ZioResponse[SnapshotCleanupRepositoryResponse] =
    doCall(request).flatMap(convertResponse[SnapshotCleanupRepositoryResponse](request))

  def execute(
    request: SnapshotCreateRequest
  ): ZioResponse[SnapshotCreateResponse] =
    doCall(request).flatMap(convertResponse[SnapshotCreateResponse](request))

  def execute(
    request: SnapshotCreateRepositoryRequest
  ): ZioResponse[SnapshotCreateRepositoryResponse] =
    doCall(request).flatMap(convertResponse[SnapshotCreateRepositoryResponse](request))

  def execute(
    request: SnapshotDeleteRequest
  ): ZioResponse[SnapshotDeleteResponse] =
    doCall(request).flatMap(convertResponse[SnapshotDeleteResponse](request))

  def execute(
    request: SnapshotDeleteRepositoryRequest
  ): ZioResponse[SnapshotDeleteRepositoryResponse] =
    doCall(request).flatMap(convertResponse[SnapshotDeleteRepositoryResponse](request))

  def execute(
    request: SnapshotGetRequest
  ): ZioResponse[SnapshotGetResponse] =
    doCall(request).flatMap(convertResponse[SnapshotGetResponse](request))

  def execute(
    request: SnapshotGetRepositoryRequest
  ): ZioResponse[SnapshotGetRepositoryResponse] =
    doCall(request).flatMap(convertResponse[SnapshotGetRepositoryResponse](request))

  def execute(
    request: SnapshotRestoreRequest
  ): ZioResponse[SnapshotRestoreResponse] =
    doCall(request).flatMap(convertResponse[SnapshotRestoreResponse](request))

  def execute(
    request: SnapshotStatusRequest
  ): ZioResponse[SnapshotStatusResponse] =
    doCall(request).flatMap(convertResponse[SnapshotStatusResponse](request))

  def execute(
    request: SnapshotVerifyRepositoryRequest
  ): ZioResponse[SnapshotVerifyRepositoryResponse] =
    doCall(request).flatMap(convertResponse[SnapshotVerifyRepositoryResponse](request))

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
