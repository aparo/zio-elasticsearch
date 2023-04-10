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

package zio.elasticsearch

import zio.exception.FrameworkException
import zio.elasticsearch.client.SnapshotActionResolver
import zio.elasticsearch.requests.snapshot._
import zio.elasticsearch.responses.snapshot._
import zio.json.ast._
import zio.{ ZIO, ZLayer }
trait SnapshotService extends SnapshotActionResolver {

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
    body: Option[Json.Obj] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, SnapshotCleanupRepositoryResponse] = {
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
  ): ZIO[Any, FrameworkException, SnapshotCleanupRepositoryResponse] = execute(request)

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
    body: Option[Json.Obj] = None,
    masterTimeout: Option[String] = None,
    waitForCompletion: Boolean = false
  ): ZIO[Any, FrameworkException, SnapshotCreateResponse] = {
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
  ): ZIO[Any, FrameworkException, SnapshotCreateResponse] = execute(request)

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
    body: Json.Obj,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    verify: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, SnapshotCreateRepositoryResponse] = {
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
  ): ZIO[Any, FrameworkException, SnapshotCreateRepositoryResponse] = execute(request)

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
  ): ZIO[Any, FrameworkException, SnapshotDeleteResponse] = {
    val request = SnapshotDeleteRequest(
      repository = repository,
      snapshot = snapshot,
      masterTimeout = masterTimeout
    )

    delete(request)

  }

  def delete(
    request: SnapshotDeleteRequest
  ): ZIO[Any, FrameworkException, SnapshotDeleteResponse] = execute(request)

  /*
   * Deletes a repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A comma-separated list of repository names
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def deleteRepository(
    repository: Chunk[String] = Chunk.empty,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, SnapshotDeleteRepositoryResponse] = {
    val request = SnapshotDeleteRepositoryRequest(
      repository = repository,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    deleteRepository(request)

  }

  def deleteRepository(
    request: SnapshotDeleteRepositoryRequest
  ): ZIO[Any, FrameworkException, SnapshotDeleteRepositoryResponse] = execute(request)

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
    snapshot: Chunk[String] = Chunk.empty,
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    verbose: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, SnapshotGetResponse] = {
    val request = SnapshotGetRequest(
      repository = repository,
      snapshot = snapshot,
      ignoreUnavailable = ignoreUnavailable,
      masterTimeout = masterTimeout,
      verbose = verbose
    )

    get(request)

  }

  def get(request: SnapshotGetRequest): ZIO[Any, FrameworkException, SnapshotGetResponse] =
    execute(request)

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
    repository: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, SnapshotGetRepositoryResponse] = {
    val request = SnapshotGetRepositoryRequest(
      local = local,
      masterTimeout = masterTimeout,
      repository = repository
    )

    getRepository(request)

  }

  def getRepository(
    request: SnapshotGetRepositoryRequest
  ): ZIO[Any, FrameworkException, SnapshotGetRepositoryResponse] = execute(request)

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
    body: Option[Json.Obj] = None,
    masterTimeout: Option[String] = None,
    waitForCompletion: Boolean = false
  ): ZIO[Any, FrameworkException, SnapshotRestoreResponse] = {
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
  ): ZIO[Any, FrameworkException, SnapshotRestoreResponse] = execute(request)

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
    snapshot: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, SnapshotStatusResponse] = {
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
  ): ZIO[Any, FrameworkException, SnapshotStatusResponse] = execute(request)

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
  ): ZIO[Any, FrameworkException, SnapshotVerifyRepositoryResponse] = {
    val request = SnapshotVerifyRepositoryRequest(
      repository = repository,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    verifyRepository(request)

  }

  def verifyRepository(
    request: SnapshotVerifyRepositoryRequest
  ): ZIO[Any, FrameworkException, SnapshotVerifyRepositoryResponse] = execute(request)

}

object SnapshotService {

  // services

  private case class Live(
    baseElasticSearchService: ElasticSearchService,
    httpService: ElasticSearchHttpService
  ) extends SnapshotService

  val live: ZLayer[ElasticSearchService, Nothing, SnapshotService] =
    ZLayer {
      for { baseElasticSearchService <- ZIO.service[ElasticSearchService] } yield Live(
        baseElasticSearchService,
        baseElasticSearchService.httpService
      )
    }

  // access methods

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
    body: Option[Json.Obj] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[SnapshotService, FrameworkException, SnapshotCleanupRepositoryResponse] =
    ZIO.environmentWithZIO[SnapshotService](
      _.get.cleanupRepository(repository = repository, body = body, masterTimeout = masterTimeout, timeout = timeout)
    )

  def cleanupRepository(
    request: SnapshotCleanupRepositoryRequest
  ): ZIO[SnapshotService, FrameworkException, SnapshotCleanupRepositoryResponse] =
    ZIO.environmentWithZIO[SnapshotService](_.get.execute(request))

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
    body: Option[Json.Obj] = None,
    masterTimeout: Option[String] = None,
    waitForCompletion: Boolean = false
  ): ZIO[SnapshotService, FrameworkException, SnapshotCreateResponse] =
    ZIO.environmentWithZIO[SnapshotService](
      _.get.create(
        repository = repository,
        snapshot = snapshot,
        body = body,
        masterTimeout = masterTimeout,
        waitForCompletion = waitForCompletion
      )
    )

  def create(request: SnapshotCreateRequest): ZIO[SnapshotService, FrameworkException, SnapshotCreateResponse] =
    ZIO.environmentWithZIO[SnapshotService](_.get.execute(request))

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
    body: Json.Obj,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    verify: Option[Boolean] = None
  ): ZIO[SnapshotService, FrameworkException, SnapshotCreateRepositoryResponse] =
    ZIO.environmentWithZIO[SnapshotService](
      _.get.createRepository(
        repository = repository,
        body = body,
        masterTimeout = masterTimeout,
        timeout = timeout,
        verify = verify
      )
    )

  def createRepository(
    request: SnapshotCreateRepositoryRequest
  ): ZIO[SnapshotService, FrameworkException, SnapshotCreateRepositoryResponse] =
    ZIO.environmentWithZIO[SnapshotService](_.get.execute(request))

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
  ): ZIO[SnapshotService, FrameworkException, SnapshotDeleteResponse] =
    ZIO.environmentWithZIO[SnapshotService](
      _.get.delete(repository = repository, snapshot = snapshot, masterTimeout = masterTimeout)
    )

  def delete(request: SnapshotDeleteRequest): ZIO[SnapshotService, FrameworkException, SnapshotDeleteResponse] =
    ZIO.environmentWithZIO[SnapshotService](_.get.execute(request))

  /*
   * Deletes a repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A comma-separated list of repository names
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def deleteRepository(
    repository: Chunk[String] = Chunk.empty,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[SnapshotService, FrameworkException, SnapshotDeleteRepositoryResponse] =
    ZIO.environmentWithZIO[SnapshotService](
      _.get.deleteRepository(repository = repository, masterTimeout = masterTimeout, timeout = timeout)
    )

  def deleteRepository(
    request: SnapshotDeleteRepositoryRequest
  ): ZIO[SnapshotService, FrameworkException, SnapshotDeleteRepositoryResponse] =
    ZIO.environmentWithZIO[SnapshotService](_.get.execute(request))

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
    snapshot: Chunk[String] = Chunk.empty,
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    verbose: Option[Boolean] = None
  ): ZIO[SnapshotService, FrameworkException, SnapshotGetResponse] =
    ZIO.environmentWithZIO[SnapshotService](
      _.get.get(
        repository = repository,
        snapshot = snapshot,
        ignoreUnavailable = ignoreUnavailable,
        masterTimeout = masterTimeout,
        verbose = verbose
      )
    )

  def get(request: SnapshotGetRequest): ZIO[SnapshotService, FrameworkException, SnapshotGetResponse] =
    ZIO.environmentWithZIO[SnapshotService](_.get.execute(request))

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
    repository: Chunk[String] = Chunk.empty
  ): ZIO[SnapshotService, FrameworkException, SnapshotGetRepositoryResponse] =
    ZIO.environmentWithZIO[SnapshotService](
      _.get.getRepository(local = local, masterTimeout = masterTimeout, repository = repository)
    )

  def getRepository(
    request: SnapshotGetRepositoryRequest
  ): ZIO[SnapshotService, FrameworkException, SnapshotGetRepositoryResponse] =
    ZIO.environmentWithZIO[SnapshotService](_.get.execute(request))

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
    body: Option[Json.Obj] = None,
    masterTimeout: Option[String] = None,
    waitForCompletion: Boolean = false
  ): ZIO[SnapshotService, FrameworkException, SnapshotRestoreResponse] =
    ZIO.environmentWithZIO[SnapshotService](
      _.get.restore(
        repository = repository,
        snapshot = snapshot,
        body = body,
        masterTimeout = masterTimeout,
        waitForCompletion = waitForCompletion
      )
    )

  def restore(request: SnapshotRestoreRequest): ZIO[SnapshotService, FrameworkException, SnapshotRestoreResponse] =
    ZIO.environmentWithZIO[SnapshotService](_.get.execute(request))

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
    snapshot: Chunk[String] = Chunk.empty
  ): ZIO[SnapshotService, FrameworkException, SnapshotStatusResponse] =
    ZIO.environmentWithZIO[SnapshotService](
      _.get.status(
        ignoreUnavailable = ignoreUnavailable,
        masterTimeout = masterTimeout,
        repository = repository,
        snapshot = snapshot
      )
    )

  def status(request: SnapshotStatusRequest): ZIO[SnapshotService, FrameworkException, SnapshotStatusResponse] =
    ZIO.environmentWithZIO[SnapshotService](_.get.execute(request))

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
  ): ZIO[SnapshotService, FrameworkException, SnapshotVerifyRepositoryResponse] =
    ZIO.environmentWithZIO[SnapshotService](
      _.get.verifyRepository(repository = repository, masterTimeout = masterTimeout, timeout = timeout)
    )

  def verifyRepository(
    request: SnapshotVerifyRepositoryRequest
  ): ZIO[SnapshotService, FrameworkException, SnapshotVerifyRepositoryResponse] =
    ZIO.environmentWithZIO[SnapshotService](_.get.execute(request))

}
