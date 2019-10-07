/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.managers

import elasticsearch.requests.cluster._
import elasticsearch.responses.cluster._
import elasticsearch.{ ExpandWildcards, Level, WaitForStatus }
import elasticsearch.ElasticSearch
import io.circe._
import elasticsearch.ZioResponse

class ClusterManager(client: ElasticSearch) {

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-health.html
   *
   * @param index Limit the information returned to a specific index
   * @param waitForNodes Wait until the specified number of nodes is available
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param waitForRelocatingShards Wait until the specified number of relocating shards is finished
   * @param waitForStatus Wait until cluster is in a specific state
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   * @param waitForActiveShards Wait until the specified number of shards is active
   * @param level Specify the level of detail for returned information
   */
  def health(
    indices: Seq[String] = Nil,
    waitForNodes: Option[String] = None,
    local: Boolean = false,
    waitForNoRelocatingShards: Option[Int] = None,
    waitForStatus: Option[WaitForStatus] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    waitForActiveShards: Option[Int] = None,
    level: Level = Level.cluster
  ): ZioResponse[ClusterHealthResponse] = {
    val request = ClusterHealthRequest(
      indices = indices,
      waitForNodes = waitForNodes,
      local = local,
      waitForNoRelocatingShards = waitForNoRelocatingShards,
      waitForStatus = waitForStatus,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards,
      level = level
    )
    health(request)

  }

  def health(
    request: ClusterHealthRequest
  ): ZioResponse[ClusterHealthResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-pending.html
   *
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Specify timeout for connection to master
   */
  def pendingTasks(
    local: Boolean = false,
    masterTimeout: Option[String] = None
  ): ZioResponse[PendingClusterTasksResponse] = {
    val request =
      PendingClusterTasksRequest(local = local, masterTimeout = masterTimeout)
    pendingTasks(request)

  }

  def pendingTasks(
    request: PendingClusterTasksRequest
  ): ZioResponse[PendingClusterTasksResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-update-settings.html
   *
   * @param body body the body of the call
   * @param flatSettings Return settings in flat format (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def putSettings(
    body: Json,
    flatSettings: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZioResponse[ClusterUpdateSettingsResponse] = {
    val request = ClusterUpdateSettingsRequest(
      body = body,
      flatSettings = flatSettings,
      masterTimeout = masterTimeout,
      timeout = timeout
    )
    putSettings(request)

  }

  def putSettings(
    request: ClusterUpdateSettingsRequest
  ): ZioResponse[ClusterUpdateSettingsResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-reroute.html
   *
   * @param body body the body of the call
   * @param explain Return an explanation of why the commands can or cannot be executed
   * @param dryRun Simulate the operation only and return the resulting state
   * @param metric Limit the information returned to the specified metrics. Defaults to all but metadata
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def reroute(
    body: Json,
    explain: Option[Boolean] = None,
    dryRun: Option[Boolean] = None,
    metric: Seq[String] = Nil,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZioResponse[ClusterRerouteResponse] = {
    val request = ClusterRerouteRequest(
      body = body,
      explain = explain,
      dryRun = dryRun,
      metric = metric,
      masterTimeout = masterTimeout,
      timeout = timeout
    )
    reroute(request)

  }

  def reroute(
    request: ClusterRerouteRequest
  ): ZioResponse[ClusterRerouteResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-state.html
   *
   * @param metric Limit the information returned to the specified metrics
   * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
   * @param masterTimeout Specify timeout for connection to master
   * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
   * @param flatSettings Return settings in flat format (default: false)
   */
  def state(
    metric: Option[String] = None,
    indices: Seq[String] = Nil,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    local: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    ignoreUnavailable: Option[Boolean] = None,
    flatSettings: Boolean = false
  ): ZioResponse[ClusterStateResponse] = {
    val request = ClusterStateRequest(
      metric = metric,
      indices = indices,
      expandWildcards = expandWildcards,
      local = local,
      allowNoIndices = allowNoIndices,
      masterTimeout = masterTimeout,
      ignoreUnavailable = ignoreUnavailable,
      flatSettings = flatSettings
    )
    state(request)

  }

  def state(
    request: ClusterStateRequest
  ): ZioResponse[ClusterStateResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-stats.html
   *
   * @param nodeIds A list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param flatSettings Return settings in flat format (default: false)
   * @param human Whether to return time and byte values in human-readable format.
   * @param timeout Explicit operation timeout
   */
  def stats(
    nodeIds: Seq[String] = Nil,
    flatSettings: Boolean = false,
    timeout: Option[String] = None
  ): ZioResponse[ClusterStatsResponse] = {
    val request =
      ClusterStatsRequest(
        nodeIds = nodeIds,
        flatSettings = flatSettings,
        timeout = timeout
      )
    stats(request)

  }

  def stats(
    request: ClusterStatsRequest
  ): ZioResponse[ClusterStatsResponse] =
    client.execute(request)

}
