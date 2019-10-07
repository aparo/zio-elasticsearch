/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.managers

import elasticsearch.ElasticSearch
import elasticsearch.requests.snapshot._
import elasticsearch.responses.snapshot._
import io.circe._
import elasticsearch.ZioResponse

class SnapshotManager(client: ElasticSearch) {

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param snapshot A snapshot name
   * @param body body the body of the call
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param waitForCompletion Should this request wait until the operation has completed before returning
   */
  def create(
    repository: String,
    snapshot: String,
    body: Json,
    masterTimeout: Option[String] = None,
    waitForCompletion: Boolean = false
  ): ZioResponse[CreateSnapshotResponse] = {
    val request = CreateSnapshotRequest(
      repository = repository,
      snapshot = snapshot,
      body = body,
      masterTimeout = masterTimeout,
      waitForCompletion = waitForCompletion
    )

    create(request)

  }

  def create(
    request: CreateSnapshotRequest
  ): ZioResponse[CreateSnapshotResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param body body the body of the call
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   * @param verify Whether to verify the repository after creation
   */
  def createRepository(
    repository: String,
    body: Json,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    verify: Option[Boolean] = None
  ): ZioResponse[PutRepositoryResponse] = {
    val request = PutRepositoryRequest(
      repository = repository,
      body = body,
      masterTimeout = masterTimeout,
      timeout = timeout,
      verify = verify
    )

    createRepository(request)

  }

  def createRepository(
    request: PutRepositoryRequest
  ): ZioResponse[PutRepositoryResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param snapshot A snapshot name
   * @param masterTimeout Explicit operation timeout for connection to master node
   */
  def delete(
    repository: String,
    snapshot: String,
    masterTimeout: Option[String] = None
  ): ZioResponse[DeleteSnapshotResponse] = {
    val request = DeleteSnapshotRequest(
      repository = repository,
      snapshot = snapshot,
      masterTimeout = masterTimeout
    )

    delete(request)

  }

  def delete(
    request: DeleteSnapshotRequest
  ): ZioResponse[DeleteSnapshotResponse] =
    client.execute(request)

  //  def deleteRepository(request: DeleteRepositoryRequest): EitherT[Future, QDBException, DeleteRepositoryResponse] =
  //    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A list of repository names
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def deleteRepository(
    repository: Seq[String] = Nil,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZioResponse[DeleteRepositoryResponse] = {
    val request = DeleteRepositoryRequest(
      repository = repository,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    deleteRepository(request)

  }

  def deleteRepository(
    request: DeleteRepositoryRequest
  ): ZioResponse[DeleteRepositoryResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param snapshot A list of snapshot names
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param ignoreUnavailable Whether to ignore unavailable snapshots, defaults to false which means a SnapshotMissingException is thrown
   */
  def get(
    repository: String,
    snapshot: Seq[String] = Nil,
    masterTimeout: Option[String] = None,
    ignoreUnavailable: Option[Boolean] = None
  ): ZioResponse[GetSnapshotsResponse] = {
    val request = GetSnapshotsRequest(
      repository = repository,
      snapshot = snapshot,
      masterTimeout = masterTimeout,
      ignoreUnavailable = ignoreUnavailable
    )

    get(request)

  }

  def get(
    request: GetSnapshotsRequest
  ): ZioResponse[GetSnapshotsResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A list of repository names
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def getRepository(
    repository: Seq[String] = Nil,
    masterTimeout: Option[String] = None,
    local: Boolean = false
  ): ZioResponse[GetRepositoriesResponse] = {
    val request =
      GetRepositoriesRequest(
        repository = repository,
        masterTimeout = masterTimeout,
        local = local
      )

    getRepository(request)

  }

  def getRepository(
    request: GetRepositoriesRequest
  ): ZioResponse[GetRepositoriesResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param snapshot A snapshot name
   * @param body body the body of the call
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param waitForCompletion Should this request wait until the operation has completed before returning
   */
  def restore(
    repository: String,
    snapshot: String,
    body: Json,
    masterTimeout: Option[String] = None,
    waitForCompletion: Boolean = false
  ): ZioResponse[RestoreSnapshotResponse] = {
    val request = RestoreSnapshotRequest(
      repository = repository,
      snapshot = snapshot,
      body = body,
      masterTimeout = masterTimeout,
      waitForCompletion = waitForCompletion
    )

    restore(request)

  }

  def restore(
    request: RestoreSnapshotRequest
  ): ZioResponse[RestoreSnapshotResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param snapshot A list of snapshot names
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param ignoreUnavailable Whether to ignore unavailable snapshots, defaults to false which means a SnapshotMissingException is thrown
   */
  def status(
    repository: Option[String] = None,
    snapshot: Seq[String] = Nil,
    masterTimeout: Option[String] = None,
    ignoreUnavailable: Option[Boolean] = None
  ): ZioResponse[SnapshotsStatusResponse] = {
    val request = SnapshotsStatusRequest(
      repository = repository,
      snapshot = snapshot,
      masterTimeout = masterTimeout,
      ignoreUnavailable = ignoreUnavailable
    )

    status(request)

  }

  def status(
    request: SnapshotsStatusRequest
  ): ZioResponse[SnapshotsStatusResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def verifyRepository(
    repository: String,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZioResponse[VerifyRepositoryResponse] = {
    val request = VerifyRepositoryRequest(
      repository = repository,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    verifyRepository(request)

  }

  def verifyRepository(
    request: VerifyRepositoryRequest
  ): ZioResponse[VerifyRepositoryResponse] =
    client.execute(request)

}
