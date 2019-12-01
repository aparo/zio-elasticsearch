package elasticsearch.client

import elasticsearch.ZioResponse
import elasticsearch.requests.snapshot._
import elasticsearch.responses.snapshot._

trait SnapshotClientActions {
  def execute(request: SnapshotCleanupRepositoryRequest)
  : ZioResponse[SnapshotCleanupRepositoryResponse]
  def execute(
               request: SnapshotCreateRequest): ZioResponse[SnapshotCreateResponse]
  def execute(request: SnapshotCreateRepositoryRequest)
  : ZioResponse[SnapshotCreateRepositoryResponse]
  def execute(
               request: SnapshotDeleteRequest): ZioResponse[SnapshotDeleteResponse]
  def execute(request: SnapshotDeleteRepositoryRequest)
  : ZioResponse[SnapshotDeleteRepositoryResponse]
  def execute(request: SnapshotGetRequest): ZioResponse[SnapshotGetResponse]
  def execute(request: SnapshotGetRepositoryRequest)
  : ZioResponse[SnapshotGetRepositoryResponse]
  def execute(
               request: SnapshotRestoreRequest): ZioResponse[SnapshotRestoreResponse]
  def execute(
               request: SnapshotStatusRequest): ZioResponse[SnapshotStatusResponse]
  def execute(request: SnapshotVerifyRepositoryRequest)
  : ZioResponse[SnapshotVerifyRepositoryResponse]

}
