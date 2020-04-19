/*
 * Copyright 2019-2020 Alberto Paro
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

}
