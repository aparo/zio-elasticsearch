/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.managers

import elasticsearch._
import io.circe._
import elasticsearch.ZioResponse
import elasticsearch.requests.snapshot._
import elasticsearch.responses.snapshot._

class SnapshotManager(client: ElasticSearch) {

  /*
   * Removes stale data from repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param body body the body of the call
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def cleanupRepository(
    repository: String,
    body: Option[JsonObject] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZioResponse[SnapshotCleanupRepositoryResponse] = {
    val request = SnapshotCleanupRepositoryRequest(
      repository = repository,
      body = body,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    cleanupRepository(request)

  }

  def cleanupRepository(
    request: SnapshotCleanupRepositoryRequest
  ): ZioResponse[SnapshotCleanupRepositoryResponse] = client.execute(request)

  /*
   * Creates a snapshot in a repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
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
    body: Option[JsonObject] = None,
    masterTimeout: Option[String] = None,
    waitForCompletion: Boolean = false
  ): ZioResponse[SnapshotCreateResponse] = {
    val request = SnapshotCreateRequest(
      repository = repository,
      snapshot = snapshot,
      body = body,
      masterTimeout = masterTimeout,
      waitForCompletion = waitForCompletion
    )

    create(request)

  }

  def create(
    request: SnapshotCreateRequest
  ): ZioResponse[SnapshotCreateResponse] = client.execute(request)

  /*
   * Creates a repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param body body the body of the call
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   * @param verify Whether to verify the repository after creation
   */
  def createRepository(
    repository: String,
    body: JsonObject,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    verify: Option[Boolean] = None
  ): ZioResponse[SnapshotCreateRepositoryResponse] = {
    val request = SnapshotCreateRepositoryRequest(
      repository = repository,
      body = body,
      masterTimeout = masterTimeout,
      timeout = timeout,
      verify = verify
    )

    createRepository(request)

  }

  def createRepository(
    request: SnapshotCreateRepositoryRequest
  ): ZioResponse[SnapshotCreateRepositoryResponse] = client.execute(request)

  /*
   * Deletes a snapshot.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param snapshot A snapshot name
   * @param masterTimeout Explicit operation timeout for connection to master node
   */
  def delete(
    repository: String,
    snapshot: String,
    masterTimeout: Option[String] = None
  ): ZioResponse[SnapshotDeleteResponse] = {
    val request = SnapshotDeleteRequest(
      repository = repository,
      snapshot = snapshot,
      masterTimeout = masterTimeout
    )

    delete(request)

  }

  def delete(
    request: SnapshotDeleteRequest
  ): ZioResponse[SnapshotDeleteResponse] = client.execute(request)

  /*
   * Deletes a repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A comma-separated list of repository names
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def deleteRepository(
    repository: Seq[String] = Nil,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZioResponse[SnapshotDeleteRepositoryResponse] = {
    val request = SnapshotDeleteRepositoryRequest(
      repository = repository,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    deleteRepository(request)

  }

  def deleteRepository(
    request: SnapshotDeleteRepositoryRequest
  ): ZioResponse[SnapshotDeleteRepositoryResponse] = client.execute(request)

  /*
   * Returns information about a snapshot.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param snapshot A comma-separated list of snapshot names
   * @param ignoreUnavailable Whether to ignore unavailable snapshots, defaults to false which means a SnapshotMissingException is thrown
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param verbose Whether to show verbose snapshot info or only show the basic info found in the repository index blob
   */
  def get(
    repository: String,
    snapshot: Seq[String] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    verbose: Option[Boolean] = None
  ): ZioResponse[SnapshotGetResponse] = {
    val request = SnapshotGetRequest(
      repository = repository,
      snapshot = snapshot,
      ignoreUnavailable = ignoreUnavailable,
      masterTimeout = masterTimeout,
      verbose = verbose
    )

    get(request)

  }

  def get(request: SnapshotGetRequest): ZioResponse[SnapshotGetResponse] =
    client.execute(request)

  /*
   * Returns information about a repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param repository A comma-separated list of repository names
   */
  def getRepository(
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    repository: Seq[String] = Nil
  ): ZioResponse[SnapshotGetRepositoryResponse] = {
    val request = SnapshotGetRepositoryRequest(
      local = local,
      masterTimeout = masterTimeout,
      repository = repository
    )

    getRepository(request)

  }

  def getRepository(
    request: SnapshotGetRepositoryRequest
  ): ZioResponse[SnapshotGetRepositoryResponse] = client.execute(request)

  /*
   * Restores a snapshot.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
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
    body: Option[JsonObject] = None,
    masterTimeout: Option[String] = None,
    waitForCompletion: Boolean = false
  ): ZioResponse[SnapshotRestoreResponse] = {
    val request = SnapshotRestoreRequest(
      repository = repository,
      snapshot = snapshot,
      body = body,
      masterTimeout = masterTimeout,
      waitForCompletion = waitForCompletion
    )

    restore(request)

  }

  def restore(
    request: SnapshotRestoreRequest
  ): ZioResponse[SnapshotRestoreResponse] = client.execute(request)

  /*
   * Returns information about the status of a snapshot.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param ignoreUnavailable Whether to ignore unavailable snapshots, defaults to false which means a SnapshotMissingException is thrown
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param repository A repository name
   * @param snapshot A comma-separated list of snapshot names
   */
  def status(
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    repository: Option[String] = None,
    snapshot: Seq[String] = Nil
  ): ZioResponse[SnapshotStatusResponse] = {
    val request = SnapshotStatusRequest(
      ignoreUnavailable = ignoreUnavailable,
      masterTimeout = masterTimeout,
      repository = repository,
      snapshot = snapshot
    )

    status(request)

  }

  def status(
    request: SnapshotStatusRequest
  ): ZioResponse[SnapshotStatusResponse] = client.execute(request)

  /*
   * Verifies a repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def verifyRepository(
    repository: String,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZioResponse[SnapshotVerifyRepositoryResponse] = {
    val request = SnapshotVerifyRepositoryRequest(
      repository = repository,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    verifyRepository(request)

  }

  def verifyRepository(
    request: SnapshotVerifyRepositoryRequest
  ): ZioResponse[SnapshotVerifyRepositoryResponse] = client.execute(request)

}
