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
import elasticsearch.requests.snapshot._
import elasticsearch.responses.snapshot._

trait SnapshotClientActions {
  def execute(request: SnapshotCleanupRepositoryRequest): ZioResponse[SnapshotCleanupRepositoryResponse]
  def execute(request: SnapshotCreateRequest): ZioResponse[SnapshotCreateResponse]
  def execute(request: SnapshotCreateRepositoryRequest): ZioResponse[SnapshotCreateRepositoryResponse]
  def execute(request: SnapshotDeleteRequest): ZioResponse[SnapshotDeleteResponse]
  def execute(request: SnapshotDeleteRepositoryRequest): ZioResponse[SnapshotDeleteRepositoryResponse]
  def execute(request: SnapshotGetRequest): ZioResponse[SnapshotGetResponse]
  def execute(request: SnapshotGetRepositoryRequest): ZioResponse[SnapshotGetRepositoryResponse]
  def execute(request: SnapshotRestoreRequest): ZioResponse[SnapshotRestoreResponse]
  def execute(request: SnapshotStatusRequest): ZioResponse[SnapshotStatusResponse]
  def execute(request: SnapshotVerifyRepositoryRequest): ZioResponse[SnapshotVerifyRepositoryResponse]

}
