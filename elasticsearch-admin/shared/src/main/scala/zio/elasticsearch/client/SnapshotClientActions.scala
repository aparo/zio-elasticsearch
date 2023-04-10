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
import zio.elasticsearch.requests.snapshot._
import zio.elasticsearch.responses.snapshot._

trait SnapshotClientActions {
  def execute(
    request: SnapshotCleanupRepositoryRequest
  ): ZIO[Any, FrameworkException, SnapshotCleanupRepositoryResponse]
  def execute(request: SnapshotCreateRequest): ZIO[Any, FrameworkException, SnapshotCreateResponse]
  def execute(request: SnapshotCreateRepositoryRequest): ZIO[Any, FrameworkException, SnapshotCreateRepositoryResponse]
  def execute(request: SnapshotDeleteRequest): ZIO[Any, FrameworkException, SnapshotDeleteResponse]
  def execute(request: SnapshotDeleteRepositoryRequest): ZIO[Any, FrameworkException, SnapshotDeleteRepositoryResponse]
  def execute(request: SnapshotGetRequest): ZIO[Any, FrameworkException, SnapshotGetResponse]
  def execute(request: SnapshotGetRepositoryRequest): ZIO[Any, FrameworkException, SnapshotGetRepositoryResponse]
  def execute(request: SnapshotRestoreRequest): ZIO[Any, FrameworkException, SnapshotRestoreResponse]
  def execute(request: SnapshotStatusRequest): ZIO[Any, FrameworkException, SnapshotStatusResponse]
  def execute(request: SnapshotVerifyRepositoryRequest): ZIO[Any, FrameworkException, SnapshotVerifyRepositoryResponse]

}
