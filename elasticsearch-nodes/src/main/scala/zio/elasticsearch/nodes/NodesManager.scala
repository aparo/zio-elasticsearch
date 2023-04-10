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

package zio.elasticsearch.nodes

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._
import zio.elasticsearch.nodes.clear_repositories_metering_archive.ClearRepositoriesMeteringArchiveRequest
import zio.elasticsearch.nodes.clear_repositories_metering_archive.ClearRepositoriesMeteringArchiveResponse
import zio.elasticsearch.nodes.get_repositories_metering_info.GetRepositoriesMeteringInfoRequest
import zio.elasticsearch.nodes.get_repositories_metering_info.GetRepositoriesMeteringInfoResponse
import zio.elasticsearch.nodes.hot_threads.HotThreadsRequest
import zio.elasticsearch.nodes.hot_threads.HotThreadsResponse
import zio.elasticsearch.nodes.info.InfoRequest
import zio.elasticsearch.nodes.info.InfoResponse
import zio.elasticsearch.nodes.reload_secure_settings.ReloadSecureSettingsRequest
import zio.elasticsearch.nodes.reload_secure_settings.ReloadSecureSettingsResponse
import zio.elasticsearch.nodes.requests.ReloadSecureSettingsRequestBody
import zio.elasticsearch.nodes.stats.StatsRequest
import zio.elasticsearch.nodes.stats.StatsResponse
import zio.elasticsearch.nodes.usage.UsageRequest
import zio.elasticsearch.nodes.usage.UsageResponse
import zio.elasticsearch.sort.Sort.Sort

object NodesManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, NodesManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new NodesManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait NodesManager {
  def httpService: ElasticSearchHttpService

  /*
   * Removes the archived repositories metering information present in the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/clear-repositories-metering-archive-api.html
   *
   * @param nodeId Comma-separated list of node IDs or names used to limit returned information.
   * @param maxArchiveVersion Specifies the maximum archive_version to be cleared from the archive.
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

   */
  def clearRepositoriesMeteringArchive(
    nodeId: String,
    maxArchiveVersion: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ClearRepositoriesMeteringArchiveResponse] = {
    val request = ClearRepositoriesMeteringArchiveRequest(
      nodeId = nodeId,
      maxArchiveVersion = maxArchiveVersion,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    clearRepositoriesMeteringArchive(request)

  }

  def clearRepositoriesMeteringArchive(
    request: ClearRepositoriesMeteringArchiveRequest
  ): ZIO[Any, FrameworkException, ClearRepositoriesMeteringArchiveResponse] =
    httpService.execute[Json, ClearRepositoriesMeteringArchiveResponse](request)

  /*
   * Returns cluster repositories metering information.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-repositories-metering-api.html
   *
   * @param nodeId A comma-separated list of node IDs or names to limit the returned information.
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

   */
  def getRepositoriesMeteringInfo(
    nodeId: Chunk[String] = Chunk.empty,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetRepositoriesMeteringInfoResponse] = {
    val request = GetRepositoriesMeteringInfoRequest(
      nodeId = nodeId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getRepositoriesMeteringInfo(request)

  }

  def getRepositoriesMeteringInfo(
    request: GetRepositoriesMeteringInfoRequest
  ): ZIO[Any, FrameworkException, GetRepositoriesMeteringInfoResponse] =
    httpService.execute[Json, GetRepositoriesMeteringInfoResponse](request)

  /*
   * Returns information about hot threads on each node in the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-hot-threads.html
   *
   * @param masterTimeout Period to wait for a connection to the master node. If no response
   * is received before the timeout expires, the request fails and
   * returns an error.
   * @server_default 30s

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

   * @param ignoreIdleThreads Don't show threads that are in known-idle places, such as waiting on a socket select or pulling from an empty task queue (default: true)
   * @param interval The interval for the second sampling of threads
   * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param snapshots Number of samples of thread stacktrace (default: 10)
   * @param sort The sort order for 'cpu' type (default: total)
   * @param threads Specify the number of threads to provide information for (default: 3)
   * @param timeout Explicit operation timeout
   * @param `type` The type to sample (default: cpu)
   */
  def hotThreads(
    masterTimeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    ignoreIdleThreads: Option[Boolean] = None,
    interval: Option[String] = None,
    nodeId: Chunk[String] = Chunk.empty,
    snapshots: Option[Double] = None,
    sort: Option[Sort] = None,
    threads: Option[Double] = None,
    timeout: Option[String] = None,
    `type`: Option[String] = None
  ): ZIO[Any, FrameworkException, HotThreadsResponse] = {
    val request = HotThreadsRequest(
      masterTimeout = masterTimeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      ignoreIdleThreads = ignoreIdleThreads,
      interval = interval,
      nodeId = nodeId,
      snapshots = snapshots,
      sort = sort,
      threads = threads,
      timeout = timeout,
      `type` = `type`
    )

    hotThreads(request)

  }

  def hotThreads(
    request: HotThreadsRequest
  ): ZIO[Any, FrameworkException, HotThreadsResponse] =
    httpService.execute[Json, HotThreadsResponse](request)

  /*
   * Returns information about nodes in the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-info.html
   *
   * @param masterTimeout Period to wait for a connection to the master node. If no response is received before the timeout expires, the request fails and returns an error.
   * @server_default 30s

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

   * @param flatSettings Return settings in flat format (default: false)
   * @param metric A comma-separated list of metrics you wish returned. Use `_all` to retrieve all metrics and `_none` to retrieve the node identity without any additional metrics.
   * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param timeout Explicit operation timeout
   */
  def info(
    masterTimeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    flatSettings: Option[Boolean] = None,
    metric: Chunk[String] = Chunk.empty,
    nodeId: Chunk[String] = Chunk.empty,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, InfoResponse] = {
    val request = InfoRequest(
      masterTimeout = masterTimeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      flatSettings = flatSettings,
      metric = metric,
      nodeId = nodeId,
      timeout = timeout
    )

    info(request)

  }

  def info(request: InfoRequest): ZIO[Any, FrameworkException, InfoResponse] =
    httpService.execute[Json, InfoResponse](request)

  /*
   * Reloads secure settings.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/secure-settings.html#reloadable-secure-settings
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

   * @param body body the body of the call
   * @param nodeId A comma-separated list of node IDs to span the reload/reinit call. Should stay empty because reloading usually involves all cluster nodes.
   * @param timeout Explicit operation timeout
   */
  def reloadSecureSettings(
    body: ReloadSecureSettingsRequestBody = ReloadSecureSettingsRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    nodeId: Chunk[String] = Chunk.empty,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, ReloadSecureSettingsResponse] = {
    val request = ReloadSecureSettingsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      nodeId = nodeId,
      timeout = timeout
    )

    reloadSecureSettings(request)

  }

  def reloadSecureSettings(
    request: ReloadSecureSettingsRequest
  ): ZIO[Any, FrameworkException, ReloadSecureSettingsResponse] =
    httpService.execute[ReloadSecureSettingsRequestBody, ReloadSecureSettingsResponse](
      request
    )

  /*
   * Returns statistical information about nodes in the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-stats.html
   *
   * @param masterTimeout Period to wait for a connection to the master node. If no response is received before the timeout expires, the request fails and returns an error.
   * @server_default 30s

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

   * @param completionFields A comma-separated list of fields for the `completion` index metric (supports wildcards)
   * @param fielddataFields A comma-separated list of fields for the `fielddata` index metric (supports wildcards)
   * @param fields A comma-separated list of fields for `fielddata` and `completion` index metric (supports wildcards)
   * @param groups A comma-separated list of search groups for `search` index metric
   * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
   * @param includeUnloadedSegments If set to true segment stats will include stats for segments that are not currently loaded into memory
   * @param indexMetric Limit the information returned for `indices` metric to the specific index metrics. Isn't used if `indices` (or `all`) metric isn't specified.
   * @param level Return indices stats aggregated at index, node or shard level
   * @param metric Limit the information returned to the specified metrics
   * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param timeout Explicit operation timeout
   * @param types A comma-separated list of document types for the `indexing` index metric
   */
  def stats(
    masterTimeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    completionFields: Chunk[String] = Chunk.empty,
    fielddataFields: Chunk[String] = Chunk.empty,
    fields: Chunk[String] = Chunk.empty,
    groups: Chunk[String] = Chunk.empty,
    includeSegmentFileSizes: Boolean = false,
    includeUnloadedSegments: Boolean = false,
    indexMetric: Option[String] = None,
    level: Level = Level.node,
    metric: Option[String] = None,
    nodeId: Chunk[String] = Chunk.empty,
    timeout: Option[String] = None,
    types: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, StatsResponse] = {
    val request = StatsRequest(
      masterTimeout = masterTimeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      completionFields = completionFields,
      fielddataFields = fielddataFields,
      fields = fields,
      groups = groups,
      includeSegmentFileSizes = includeSegmentFileSizes,
      includeUnloadedSegments = includeUnloadedSegments,
      indexMetric = indexMetric,
      level = level,
      metric = metric,
      nodeId = nodeId,
      timeout = timeout,
      types = types
    )

    stats(request)

  }

  def stats(
    request: StatsRequest
  ): ZIO[Any, FrameworkException, StatsResponse] =
    httpService.execute[Json, StatsResponse](request)

  /*
   * Returns low-level information about REST actions usage on nodes.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-usage.html
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

   * @param metric Limit the information returned to the specified metrics
   * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param timeout Explicit operation timeout
   */
  def usage(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    metric: Option[String] = None,
    nodeId: Chunk[String] = Chunk.empty,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, UsageResponse] = {
    val request = UsageRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      metric = metric,
      nodeId = nodeId,
      timeout = timeout
    )

    usage(request)

  }

  def usage(
    request: UsageRequest
  ): ZIO[Any, FrameworkException, UsageResponse] =
    httpService.execute[Json, UsageResponse](request)

}
