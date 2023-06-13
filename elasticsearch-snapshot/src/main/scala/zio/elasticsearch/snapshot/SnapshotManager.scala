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

package zio.elasticsearch.snapshot

import zio._
import zio.elasticsearch._
import zio.elasticsearch.snapshot.cleanup_repository.{ CleanupRepositoryRequest, CleanupRepositoryResponse }
import zio.elasticsearch.snapshot.clone.{ CloneRequest, CloneResponse }
import zio.elasticsearch.snapshot.create.{ CreateRequest, CreateResponse }
import zio.elasticsearch.snapshot.create_repository.{ CreateRepositoryRequest, CreateRepositoryResponse }
import zio.elasticsearch.snapshot.delete.{ DeleteRequest, DeleteResponse }
import zio.elasticsearch.snapshot.delete_repository.{ DeleteRepositoryRequest, DeleteRepositoryResponse }
import zio.elasticsearch.snapshot.get.{ GetRequest, GetResponse }
import zio.elasticsearch.snapshot.get_repository.{ GetRepositoryRequest, GetRepositoryResponse }
import zio.elasticsearch.snapshot.repository_analyze.{ RepositoryAnalyzeRequest, RepositoryAnalyzeResponse }
import zio.elasticsearch.snapshot.requests.{
  CloneRequestBody,
  CreateRepositoryRequestBody,
  CreateRequestBody,
  RestoreRequestBody
}
import zio.elasticsearch.snapshot.restore.{ RestoreRequest, RestoreResponse }
import zio.elasticsearch.snapshot.status.{ StatusRequest, StatusResponse }
import zio.elasticsearch.snapshot.verify_repository.{ VerifyRepositoryRequest, VerifyRepositoryResponse }
import zio.exception._
import zio.json.ast._

object SnapshotManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, SnapshotManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new SnapshotManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait SnapshotManager {
  def httpService: ElasticSearchHttpService

  /*
   * Removes stale data from repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/clean-up-snapshot-repo-api.html
   *
   * @param repository A repository name
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def cleanupRepository(
    repository: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, CleanupRepositoryResponse] = {
    val request = CleanupRepositoryRequest(
      repository = repository,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    cleanupRepository(request)

  }

  def cleanupRepository(
    request: CleanupRepositoryRequest
  ): ZIO[Any, FrameworkException, CleanupRepositoryResponse] =
    httpService.execute[Json, CleanupRepositoryResponse](request)

  /*
   * Clones indices from one snapshot into another snapshot in the same repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param snapshot The name of the snapshot to clone from
   * @param targetSnapshot The name of the cloned snapshot to create
   * @param timeout

   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param masterTimeout Explicit operation timeout for connection to master node
   */
  def clone(
    repository: String,
    snapshot: String,
    targetSnapshot: String,
    body: CloneRequestBody,
    timeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, CloneResponse] = {
    val request = CloneRequest(
      repository = repository,
      snapshot = snapshot,
      targetSnapshot = targetSnapshot,
      timeout = timeout,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout
    )

    clone(request)

  }

  def clone(
    request: CloneRequest
  ): ZIO[Any, FrameworkException, CloneResponse] =
    httpService.execute[CloneRequestBody, CloneResponse](request)

  /*
   * Creates a snapshot in a repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param snapshot A snapshot name
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param waitForCompletion Should this request wait until the operation has completed before returning
   */
  def create(
    repository: String,
    snapshot: String,
    body: CreateRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    waitForCompletion: Boolean = false
  ): ZIO[Any, FrameworkException, CreateResponse] = {
    val request = CreateRequest(
      repository = repository,
      snapshot = snapshot,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      masterTimeout = masterTimeout,
      waitForCompletion = waitForCompletion
    )

    create(request)

  }

  def create(
    request: CreateRequest
  ): ZIO[Any, FrameworkException, CreateResponse] =
    httpService.execute[CreateRequestBody, CreateResponse](request)

  /*
   * Creates a repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   * @param verify Whether to verify the repository after creation
   */
  def createRepository(
    repository: String,
    body: CreateRepositoryRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    verify: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, CreateRepositoryResponse] = {
    val request = CreateRepositoryRequest(
      repository = repository,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout,
      timeout = timeout,
      verify = verify
    )

    createRepository(request)

  }

  def createRepository(
    request: CreateRepositoryRequest
  ): ZIO[Any, FrameworkException, CreateRepositoryResponse] =
    httpService.execute[CreateRepositoryRequestBody, CreateRepositoryResponse](
      request
    )

  /*
   * Deletes one or more snapshots.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param snapshot A comma-separated list of snapshot names
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param masterTimeout Explicit operation timeout for connection to master node
   */
  def delete(
    repository: String,
    snapshot: Chunk[String] = Chunk.empty,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteResponse] = {
    val request = DeleteRequest(
      repository = repository,
      snapshot = snapshot,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout
    )

    delete(request)

  }

  def delete(
    request: DeleteRequest
  ): ZIO[Any, FrameworkException, DeleteResponse] =
    httpService.execute[Json, DeleteResponse](request)

  /*
   * Deletes a repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository Name of the snapshot repository to unregister. Wildcard (`*`) patterns are supported.
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def deleteRepository(
    repository: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteRepositoryResponse] = {
    val request = DeleteRepositoryRequest(
      repository = repository,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    deleteRepository(request)

  }

  def deleteRepository(
    request: DeleteRepositoryRequest
  ): ZIO[Any, FrameworkException, DeleteRepositoryResponse] =
    httpService.execute[Json, DeleteRepositoryResponse](request)

  /*
   * Returns information about a snapshot.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param snapshot A comma-separated list of snapshot names
   * @param local

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param after Offset identifier to start pagination from as returned by the 'next' field in the response body.
   * @param fromSortValue Value of the current sort column at which to start retrieval.
   * @param ignoreUnavailable Whether to ignore unavailable snapshots, defaults to false which means a SnapshotMissingException is thrown
   * @param includeRepository Whether to include the repository name in the snapshot info. Defaults to true.
   * @param indexDetails Whether to include details of each index in the snapshot, if those details are available. Defaults to false.
   * @param indexNames Whether to include the name of each index in the snapshot. Defaults to true.
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param offset Numeric offset to start pagination based on the snapshots matching the request. Defaults to 0
   * @param order Sort order
   * @param size Maximum number of snapshots to return. Defaults to 0 which means return all that match without limit.
   * @param slmPolicyFilter Filter snapshots by a comma-separated list of SLM policy names that snapshots belong to. Accepts wildcards. Use the special pattern '_none' to match snapshots without an SLM policy
   * @param sort Allows setting a sort order for the result. Defaults to start_time
   * @param verbose Whether to show verbose snapshot info or only show the basic info found in the repository index blob
   */
  def get(
    repository: String,
    snapshot: Chunk[String] = Chunk.empty,
    local: Boolean,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    after: Option[String] = None,
    fromSortValue: Option[String] = None,
    ignoreUnavailable: Option[Boolean] = None,
    includeRepository: Option[Boolean] = None,
    indexDetails: Option[Boolean] = None,
    indexNames: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    offset: Option[Int] = None,
//    order: Order = Order.asc,
    size: Option[Int] = None,
    slmPolicyFilter: Chunk[String] = Chunk.empty,
//    sort: Sort = Sort.start_time,
    verbose: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, GetResponse] = {
    val request = GetRequest(
      repository = repository,
      snapshot = snapshot,
      local = local,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      after = after,
      fromSortValue = fromSortValue,
      ignoreUnavailable = ignoreUnavailable,
      includeRepository = includeRepository,
      indexDetails = indexDetails,
      indexNames = indexNames,
      masterTimeout = masterTimeout,
      offset = offset,
//      order = order,
      size = size,
      slmPolicyFilter = slmPolicyFilter,
//      sort = sort,
      verbose = verbose
    )

    get(request)

  }

  def get(request: GetRequest): ZIO[Any, FrameworkException, GetResponse] =
    httpService.execute[Json, GetResponse](request)

  /*
   * Returns information about a repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param repository A comma-separated list of repository names
   */
  def getRepository(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    repository: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, GetRepositoryResponse] = {
    val request = GetRepositoryRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      local = local,
      masterTimeout = masterTimeout,
      repository = repository
    )

    getRepository(request)

  }

  def getRepository(
    request: GetRepositoryRequest
  ): ZIO[Any, FrameworkException, GetRepositoryResponse] =
    httpService.execute[Json, GetRepositoryResponse](request)

  /*
   * Analyzes a repository for correctness and performance
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param blobCount Number of blobs to create during the test. Defaults to 100.
   * @param concurrency Number of operations to run concurrently during the test. Defaults to 10.
   * @param detailed Whether to return detailed results or a summary. Defaults to 'false' so that only the summary is returned.
   * @param earlyReadNodeCount Number of nodes on which to perform an early read on a blob, i.e. before writing has completed. Early reads are rare actions so the 'rare_action_probability' parameter is also relevant. Defaults to 2.
   * @param maxBlobSize Maximum size of a blob to create during the test, e.g '1gb' or '100mb'. Defaults to '10mb'.
   * @param maxTotalDataSize Maximum total size of all blobs to create during the test, e.g '1tb' or '100gb'. Defaults to '1gb'.
   * @param rareActionProbability Probability of taking a rare action such as an early read or an overwrite. Defaults to 0.02.
   * @param rarelyAbortWrites Whether to rarely abort writes before they complete. Defaults to 'true'.
   * @param readNodeCount Number of nodes on which to read a blob after writing. Defaults to 10.
   * @param seed Seed for the random number generator used to create the test workload. Defaults to a random value.
   * @param timeout Explicit operation timeout. Defaults to '30s'.
   */
  def repositoryAnalyze(
    repository: String,
    blobCount: Option[Double] = None,
    concurrency: Option[Double] = None,
    detailed: Option[Boolean] = None,
    earlyReadNodeCount: Option[Double] = None,
    maxBlobSize: Option[String] = None,
    maxTotalDataSize: Option[String] = None,
    rareActionProbability: Option[Double] = None,
    rarelyAbortWrites: Option[Boolean] = None,
    readNodeCount: Option[Double] = None,
    seed: Option[Double] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, RepositoryAnalyzeResponse] = {
    val request = RepositoryAnalyzeRequest(
      repository = repository,
      blobCount = blobCount,
      concurrency = concurrency,
      detailed = detailed,
      earlyReadNodeCount = earlyReadNodeCount,
      maxBlobSize = maxBlobSize,
      maxTotalDataSize = maxTotalDataSize,
      rareActionProbability = rareActionProbability,
      rarelyAbortWrites = rarelyAbortWrites,
      readNodeCount = readNodeCount,
      seed = seed,
      timeout = timeout
    )

    repositoryAnalyze(request)

  }

  def repositoryAnalyze(
    request: RepositoryAnalyzeRequest
  ): ZIO[Any, FrameworkException, RepositoryAnalyzeResponse] =
    httpService.execute[Json, RepositoryAnalyzeResponse](request)

  /*
   * Restores a snapshot.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param snapshot A snapshot name
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param waitForCompletion Should this request wait until the operation has completed before returning
   */
  def restore(
    repository: String,
    snapshot: String,
    body: RestoreRequestBody = RestoreRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    waitForCompletion: Boolean = false
  ): ZIO[Any, FrameworkException, RestoreResponse] = {
    val request = RestoreRequest(
      repository = repository,
      snapshot = snapshot,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      masterTimeout = masterTimeout,
      waitForCompletion = waitForCompletion
    )

    restore(request)

  }

  def restore(
    request: RestoreRequest
  ): ZIO[Any, FrameworkException, RestoreResponse] =
    httpService.execute[RestoreRequestBody, RestoreResponse](request)

  /*
   * Returns information about the status of a snapshot.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param ignoreUnavailable Whether to ignore unavailable snapshots, defaults to false which means a SnapshotMissingException is thrown
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param repository A repository name
   * @param snapshot A comma-separated list of snapshot names
   */
  def status(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    ignoreUnavailable: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    repository: Option[String] = None,
    snapshot: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, StatusResponse] = {
    val request = StatusRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      ignoreUnavailable = ignoreUnavailable,
      masterTimeout = masterTimeout,
      repository = repository,
      snapshot = snapshot
    )

    status(request)

  }

  def status(
    request: StatusRequest
  ): ZIO[Any, FrameworkException, StatusResponse] =
    httpService.execute[Json, StatusResponse](request)

  /*
   * Verifies a repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
   *
   * @param repository A repository name
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def verifyRepository(
    repository: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, VerifyRepositoryResponse] = {
    val request = VerifyRepositoryRequest(
      repository = repository,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    verifyRepository(request)

  }

  def verifyRepository(
    request: VerifyRepositoryRequest
  ): ZIO[Any, FrameworkException, VerifyRepositoryResponse] =
    httpService.execute[Json, VerifyRepositoryResponse](request)

}
