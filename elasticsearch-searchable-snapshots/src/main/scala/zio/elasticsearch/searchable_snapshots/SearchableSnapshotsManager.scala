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

package zio.elasticsearch.searchable_snapshots

import zio._
import zio.elasticsearch._
import zio.elasticsearch.common._
import zio.elasticsearch.searchable_snapshots.cache_stats.{ CacheStatsRequest, CacheStatsResponse }
import zio.elasticsearch.searchable_snapshots.clear_cache.{ ClearCacheRequest, ClearCacheResponse }
import zio.elasticsearch.searchable_snapshots.mount.{ MountRequest, MountResponse }
import zio.elasticsearch.searchable_snapshots.requests.MountRequestBody
import zio.elasticsearch.searchable_snapshots.stats.{ StatsRequest, StatsResponse }
import zio.exception._
import zio.json.ast._

object SearchableSnapshotsManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, SearchableSnapshotsManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new SearchableSnapshotsManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait SearchableSnapshotsManager {
  def httpService: ElasticSearchHttpService

  /*
   * Retrieve node-level cache statistics about searchable snapshots.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/searchable-snapshots-apis.html
   *
   * @param masterTimeout

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

   * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   */
  def cacheStats(
    masterTimeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    nodeId: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, CacheStatsResponse] = {
    val request = CacheStatsRequest(
      masterTimeout = masterTimeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      nodeId = nodeId
    )

    cacheStats(request)

  }

  def cacheStats(
    request: CacheStatsRequest
  ): ZIO[Any, FrameworkException, CacheStatsResponse] =
    httpService.execute[Json, CacheStatsResponse](request)

  /*
   * Clear the cache of searchable snapshots.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/searchable-snapshots-apis.html
   *
   * @param pretty

   * @param human

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param index A comma-separated list of index name to limit the operation
   * @param indices A comma-separated list of index names
   */
  def clearCache(
    pretty: Boolean = false,
    human: Boolean = false,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreUnavailable: Option[Boolean] = None,
    index: Chunk[String] = Chunk.empty,
    indices: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, ClearCacheResponse] = {
    val request = ClearCacheRequest(
      pretty = pretty,
      human = human,
      errorTrace = errorTrace,
      filterPath = filterPath,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreUnavailable = ignoreUnavailable,
      index = index,
      indices = indices
    )

    clearCache(request)

  }

  def clearCache(
    request: ClearCacheRequest
  ): ZIO[Any, FrameworkException, ClearCacheResponse] =
    httpService.execute[Json, ClearCacheResponse](request)

  /*
   * Mount a snapshot as a searchable index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/searchable-snapshots-api-mount-snapshot.html
   *
   * @param repository The name of the repository containing the snapshot of the index to mount
   * @param snapshot The name of the snapshot of the index to mount
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
   * @param storage Selects the kind of local storage used to accelerate searches. Experimental, and defaults to `full_copy`
   * @param waitForCompletion Should this request wait until the operation has completed before returning
   */
  def mount(
    repository: String,
    snapshot: String,
    body: MountRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    storage: String = "false",
    waitForCompletion: Boolean = false
  ): ZIO[Any, FrameworkException, MountResponse] = {
    val request = MountRequest(
      repository = repository,
      snapshot = snapshot,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout,
      storage = storage,
      waitForCompletion = waitForCompletion
    )

    mount(request)

  }

  def mount(
    request: MountRequest
  ): ZIO[Any, FrameworkException, MountResponse] =
    httpService.execute[MountRequestBody, MountResponse](request)

  /*
   * Retrieve shard-level statistics about searchable snapshots.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/searchable-snapshots-apis.html
   *
   * @param index

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

   * @param indices A comma-separated list of index names
   * @param level Return stats aggregated at cluster, index or shard level
   */
  def stats(
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    indices: Chunk[String] = Chunk.empty,
    level: Level = Level.indices
  ): ZIO[Any, FrameworkException, StatsResponse] = {
    val request = StatsRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      indices = indices,
      level = level
    )

    stats(request)

  }

  def stats(
    request: StatsRequest
  ): ZIO[Any, FrameworkException, StatsResponse] =
    httpService.execute[Json, StatsResponse](request)

}
