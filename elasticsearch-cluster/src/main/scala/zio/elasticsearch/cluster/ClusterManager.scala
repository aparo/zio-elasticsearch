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

package zio.elasticsearch.cluster

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._
import zio.elasticsearch.cluster.allocation_explain.AllocationExplainRequest
import zio.elasticsearch.cluster.allocation_explain.AllocationExplainResponse
import zio.elasticsearch.cluster.delete_component_template.DeleteComponentTemplateRequest
import zio.elasticsearch.cluster.delete_component_template.DeleteComponentTemplateResponse
import zio.elasticsearch.cluster.delete_voting_config_exclusions.DeleteVotingConfigExclusionsRequest
import zio.elasticsearch.cluster.delete_voting_config_exclusions.DeleteVotingConfigExclusionsResponse
import zio.elasticsearch.cluster.exists_component_template.ExistsComponentTemplateRequest
import zio.elasticsearch.cluster.exists_component_template.ExistsComponentTemplateResponse
import zio.elasticsearch.cluster.get_component_template.GetComponentTemplateRequest
import zio.elasticsearch.cluster.get_component_template.GetComponentTemplateResponse
import zio.elasticsearch.cluster.get_settings.GetSettingsRequest
import zio.elasticsearch.cluster.get_settings.GetSettingsResponse
import zio.elasticsearch.cluster.health.HealthRequest
import zio.elasticsearch.cluster.health.HealthResponse
import zio.elasticsearch.cluster.pending_tasks.PendingTasksRequest
import zio.elasticsearch.cluster.pending_tasks.PendingTasksResponse
import zio.elasticsearch.cluster.post_voting_config_exclusions.PostVotingConfigExclusionsRequest
import zio.elasticsearch.cluster.post_voting_config_exclusions.PostVotingConfigExclusionsResponse
import zio.elasticsearch.cluster.put_component_template.PutComponentTemplateRequest
import zio.elasticsearch.cluster.put_component_template.PutComponentTemplateResponse
import zio.elasticsearch.cluster.put_settings.PutSettingsRequest
import zio.elasticsearch.cluster.put_settings.PutSettingsResponse
import zio.elasticsearch.cluster.remote_info.RemoteInfoRequest
import zio.elasticsearch.cluster.remote_info.RemoteInfoResponse
import zio.elasticsearch.cluster.requests.{
  AllocationExplainRequestBody,
  PutComponentTemplateRequestBody,
  PutSettingsRequestBody,
  RerouteRequestBody
}
import zio.elasticsearch.cluster.reroute.RerouteRequest
import zio.elasticsearch.cluster.reroute.RerouteResponse
import zio.elasticsearch.cluster.state.StateRequest
import zio.elasticsearch.cluster.state.StateResponse
import zio.elasticsearch.cluster.stats.StatsRequest
import zio.elasticsearch.cluster.stats.StatsResponse

object ClusterManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, ClusterManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new ClusterManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait ClusterManager {
  def httpService: ElasticSearchHttpService

  /*
   * Provides explanations for shard allocations in the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-allocation-explain.html
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
   * @param includeDiskInfo Return information about disk usage and shard sizes (default: false)
   * @param includeYesDecisions Return 'YES' decisions in explanation (default: false)
   */
  def allocationExplain(
    body: AllocationExplainRequestBody = AllocationExplainRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    includeDiskInfo: Option[Boolean] = None,
    includeYesDecisions: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, AllocationExplainResponse] = {
    val request = AllocationExplainRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      includeDiskInfo = includeDiskInfo,
      includeYesDecisions = includeYesDecisions
    )

    allocationExplain(request)

  }

  def allocationExplain(request: AllocationExplainRequest): ZIO[Any, FrameworkException, AllocationExplainResponse] =
    httpService.execute[AllocationExplainRequestBody, AllocationExplainResponse](request)

  /*
   * Deletes a component template
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-component-template.html
   *
   * @param name The name of the template
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

   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def deleteComponentTemplate(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteComponentTemplateResponse] = {
    val request = DeleteComponentTemplateRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    deleteComponentTemplate(request)

  }

  def deleteComponentTemplate(
    request: DeleteComponentTemplateRequest
  ): ZIO[Any, FrameworkException, DeleteComponentTemplateResponse] =
    httpService.execute[Json, DeleteComponentTemplateResponse](request)

  /*
   * Clears cluster voting config exclusions.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/voting-config-exclusions.html
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

   * @param masterTimeout Timeout for submitting request to master
   * @param waitForRemoval Specifies whether to wait for all excluded nodes to be removed from the cluster before clearing the voting configuration exclusions list.
   */
  def deleteVotingConfigExclusions(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: String = "30s",
    waitForRemoval: Boolean = true
  ): ZIO[Any, FrameworkException, DeleteVotingConfigExclusionsResponse] = {
    val request = DeleteVotingConfigExclusionsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout,
      waitForRemoval = waitForRemoval
    )

    deleteVotingConfigExclusions(request)

  }

  def deleteVotingConfigExclusions(
    request: DeleteVotingConfigExclusionsRequest
  ): ZIO[Any, FrameworkException, DeleteVotingConfigExclusionsResponse] =
    httpService.execute[Json, DeleteVotingConfigExclusionsResponse](request)

  /*
   * Returns information about whether a particular component template exist
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-component-template.html
   *
   * @param name The name of the template
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
   */
  def existsComponentTemplate(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, ExistsComponentTemplateResponse] = {
    val request = ExistsComponentTemplateRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      local = local,
      masterTimeout = masterTimeout
    )

    existsComponentTemplate(request)

  }

  def existsComponentTemplate(
    request: ExistsComponentTemplateRequest
  ): ZIO[Any, FrameworkException, ExistsComponentTemplateResponse] =
    httpService.execute[Json, ExistsComponentTemplateResponse](request)

  /*
   * Returns one or more component templates
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-component-template.html
   *
   * @param flatSettings
@server_default false

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
   * @param name The comma separated names of the component templates
   */
  def getComponentTemplate(
    flatSettings: Boolean,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    name: Option[String] = None
  ): ZIO[Any, FrameworkException, GetComponentTemplateResponse] = {
    val request = GetComponentTemplateRequest(
      flatSettings = flatSettings,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      local = local,
      masterTimeout = masterTimeout,
      name = name
    )

    getComponentTemplate(request)

  }

  def getComponentTemplate(
    request: GetComponentTemplateRequest
  ): ZIO[Any, FrameworkException, GetComponentTemplateResponse] =
    httpService.execute[Json, GetComponentTemplateResponse](request)

  /*
   * Returns cluster settings.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-get-settings.html
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

   * @param flatSettings Return settings in flat format (default: false)
   * @param includeDefaults Whether to return all default clusters setting.
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def getSettings(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    flatSettings: Option[Boolean] = None,
    includeDefaults: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, GetSettingsResponse] = {
    val request = GetSettingsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      flatSettings = flatSettings,
      includeDefaults = includeDefaults,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    getSettings(request)

  }

  def getSettings(request: GetSettingsRequest): ZIO[Any, FrameworkException, GetSettingsResponse] =
    httpService.execute[Json, GetSettingsResponse](request)

  /*
   * Returns basic information about the health of the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-health.html
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

   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param index Limit the information returned to a specific index
   * @param level Specify the level of detail for returned information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Wait until the specified number of shards is active
   * @param waitForEvents Wait until all currently queued events with the given priority are processed
   * @param waitForNoInitializingShards Whether to wait until there are no initializing shards in the cluster
   * @param waitForNoRelocatingShards Whether to wait until there are no relocating shards in the cluster
   * @param waitForNodes Wait until the specified number of nodes is available
   * @param waitForStatus Wait until cluster is in a specific state
   */
  def health(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    index: Option[String] = None,
    level: Level = Level.cluster,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[String] = None,
    waitForEvents: Seq[WaitForEvents] = Nil,
    waitForNoInitializingShards: Option[Boolean] = None,
    waitForNoRelocatingShards: Option[Boolean] = None,
    waitForNodes: Option[String] = None,
    waitForStatus: Option[WaitForStatus] = None
  ): ZIO[Any, FrameworkException, HealthResponse] = {
    val request = HealthRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      expandWildcards = expandWildcards,
      index = index,
      level = level,
      local = local,
      masterTimeout = masterTimeout,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards,
//      waitForEvents = waitForEvents,
      waitForNoInitializingShards = waitForNoInitializingShards,
      waitForNoRelocatingShards = waitForNoRelocatingShards,
      waitForNodes = waitForNodes,
      waitForStatus = waitForStatus
    )

    health(request)

  }

  def health(request: HealthRequest): ZIO[Any, FrameworkException, HealthResponse] =
    httpService.execute[Json, HealthResponse](request)

  /*
   * Returns a list of any cluster-level changes (e.g. create index, update mapping,
allocate or fail shard) which have not yet been executed.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-pending.html
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
   * @param masterTimeout Specify timeout for connection to master
   */
  def pendingTasks(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, PendingTasksResponse] = {
    val request = PendingTasksRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      local = local,
      masterTimeout = masterTimeout
    )

    pendingTasks(request)

  }

  def pendingTasks(request: PendingTasksRequest): ZIO[Any, FrameworkException, PendingTasksResponse] =
    httpService.execute[Json, PendingTasksResponse](request)

  /*
   * Updates the cluster voting config exclusions by node ids or node names.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/voting-config-exclusions.html
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

   * @param masterTimeout Timeout for submitting request to master
   * @param nodeIds A comma-separated list of the persistent ids of the nodes to exclude from the voting configuration. If specified, you may not also specify ?node_names.
   * @param nodeNames A comma-separated list of the names of the nodes to exclude from the voting configuration. If specified, you may not also specify ?node_ids.
   * @param timeout Explicit operation timeout
   */
  def postVotingConfigExclusions(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: String = "30s",
    nodeIds: Chunk[String] = Chunk.empty,
    nodeNames: Chunk[String] = Chunk.empty,
    timeout: String = "30s"
  ): ZIO[Any, FrameworkException, PostVotingConfigExclusionsResponse] = {
    val request = PostVotingConfigExclusionsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout,
      nodeIds = nodeIds,
      nodeNames = nodeNames,
      timeout = timeout
    )

    postVotingConfigExclusions(request)

  }

  def postVotingConfigExclusions(
    request: PostVotingConfigExclusionsRequest
  ): ZIO[Any, FrameworkException, PostVotingConfigExclusionsResponse] =
    httpService.execute[Json, PostVotingConfigExclusionsResponse](request)

  /*
   * Creates or updates a component template
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-component-template.html
   *
   * @param name The name of the template
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

   * @param create Whether the index template should only be added if new or can also replace an existing one
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def putComponentTemplate(
    name: String,
    body: PutComponentTemplateRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    create: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, PutComponentTemplateResponse] = {
    val request = PutComponentTemplateRequest(
      name = name,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      create = create,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    putComponentTemplate(request)

  }

  def putComponentTemplate(
    request: PutComponentTemplateRequest
  ): ZIO[Any, FrameworkException, PutComponentTemplateResponse] =
    httpService.execute[PutComponentTemplateRequestBody, PutComponentTemplateResponse](request)

  /*
   * Updates the cluster settings.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-update-settings.html
   *
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

   * @param flatSettings Return settings in flat format (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def putSettings(
    body: PutSettingsRequestBody = PutSettingsRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    flatSettings: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, PutSettingsResponse] = {
    val request = PutSettingsRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      flatSettings = flatSettings,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    putSettings(request)

  }

  def putSettings(request: PutSettingsRequest): ZIO[Any, FrameworkException, PutSettingsResponse] =
    httpService.execute[PutSettingsRequestBody, PutSettingsResponse](request)

  /*
   * Returns the information about configured remote clusters.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-remote-info.html
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

   */
  def remoteInfo(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, RemoteInfoResponse] = {
    val request = RemoteInfoRequest(errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    remoteInfo(request)

  }

  def remoteInfo(request: RemoteInfoRequest): ZIO[Any, FrameworkException, RemoteInfoResponse] =
    httpService.execute[Json, RemoteInfoResponse](request)

  /*
   * Allows to manually change the allocation of individual shards in the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-reroute.html
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
   * @param dryRun Simulate the operation only and return the resulting state
   * @param explain Return an explanation of why the commands can or cannot be executed
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param metric Limit the information returned to the specified metrics. Defaults to all but metadata
   * @param retryFailed Retries allocation of shards that are blocked due to too many subsequent allocation failures
   * @param timeout Explicit operation timeout
   */
  def reroute(
    body: RerouteRequestBody = RerouteRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    dryRun: Option[Boolean] = None,
    explain: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    metric: Chunk[String] = Chunk.empty,
    retryFailed: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, RerouteResponse] = {
    val request = RerouteRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      dryRun = dryRun,
      explain = explain,
      masterTimeout = masterTimeout,
      metric = metric,
      retryFailed = retryFailed,
      timeout = timeout
    )

    reroute(request)

  }

  def reroute(request: RerouteRequest): ZIO[Any, FrameworkException, RerouteResponse] =
    httpService.execute[RerouteRequestBody, RerouteResponse](request)

  /*
   * Returns a comprehensive information about the state of the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-state.html
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

   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param flatSettings Return settings in flat format (default: false)
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Specify timeout for connection to master
   * @param metric Limit the information returned to the specified metrics
   * @param waitForMetadataVersion Wait for the metadata version to be equal or greater than the specified metadata version
   * @param waitForTimeout The maximum time to wait for wait_for_metadata_version before timing out
   */
  def state(
    indices: Chunk[String] = Chunk.empty,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    flatSettings: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    metric: Option[String] = None,
    waitForMetadataVersion: Option[Double] = None,
    waitForTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, StateResponse] = {
    val request = StateRequest(
      indices = indices,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      flatSettings = flatSettings,
      ignoreUnavailable = ignoreUnavailable,
      local = local,
      masterTimeout = masterTimeout,
      metric = metric,
      waitForMetadataVersion = waitForMetadataVersion,
      waitForTimeout = waitForTimeout
    )

    state(request)

  }

  def state(request: StateRequest): ZIO[Any, FrameworkException, StateResponse] =
    httpService.execute[Json, StateResponse](request)

  /*
   * Returns high-level overview of cluster statistics.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-stats.html
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

   * @param flatSettings Return settings in flat format (default: false)
   * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param timeout Explicit operation timeout
   */
  def stats(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    flatSettings: Option[Boolean] = None,
    nodeId: Chunk[String] = Chunk.empty,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, StatsResponse] = {
    val request = StatsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      flatSettings = flatSettings,
      nodeId = nodeId,
      timeout = timeout
    )

    stats(request)

  }

  def stats(request: StatsRequest): ZIO[Any, FrameworkException, StatsResponse] =
    httpService.execute[Json, StatsResponse](request)

}
