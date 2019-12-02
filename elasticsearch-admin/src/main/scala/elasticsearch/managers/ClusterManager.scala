/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.managers

import elasticsearch._
import io.circe._
import elasticsearch.ZioResponse
import elasticsearch.client.ClusterActionResolver
import elasticsearch.requests.cluster._
import elasticsearch.responses.cluster._

class ClusterManager(client: ClusterActionResolver) {

  /*
   * Provides explanations for shard allocations in the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-allocation-explain.html
   *
   * @param body body the body of the call
   * @param includeDiskInfo Return information about disk usage and shard sizes (default: false)
   * @param includeYesDecisions Return 'YES' decisions in explanation (default: false)
   */
  def allocationExplain(
    body: Option[JsonObject] = None,
    includeDiskInfo: Option[Boolean] = None,
    includeYesDecisions: Option[Boolean] = None
  ): ZioResponse[ClusterAllocationExplainResponse] = {
    val request = ClusterAllocationExplainRequest(
      body = body,
      includeDiskInfo = includeDiskInfo,
      includeYesDecisions = includeYesDecisions
    )

    allocationExplain(request)

  }

  def allocationExplain(request: ClusterAllocationExplainRequest): ZioResponse[ClusterAllocationExplainResponse] =
    client.execute(request)

  /*
   * Returns cluster settings.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-update-settings.html
   *
   * @param flatSettings Return settings in flat format (default: false)
   * @param includeDefaults Whether to return all default clusters setting.
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def getSettings(
    flatSettings: Option[Boolean] = None,
    includeDefaults: Boolean = false,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZioResponse[ClusterGetSettingsResponse] = {
    val request = ClusterGetSettingsRequest(
      flatSettings = flatSettings,
      includeDefaults = includeDefaults,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    getSettings(request)

  }

  def getSettings(request: ClusterGetSettingsRequest): ZioResponse[ClusterGetSettingsResponse] =
    client.execute(request)

  /*
   * Returns basic information about the health of the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-health.html
   *
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
    body: JsonObject = JsonObject.empty,
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
  ): ZioResponse[ClusterHealthResponse] = {
    val request = ClusterHealthRequest(
      body = body,
      index = index,
      expandWildcards = expandWildcards,
      level = level,
      local = local,
      masterTimeout = masterTimeout,
      timeout = timeout,
      waitForActiveShards = waitForActiveShards,
      waitForEvents = waitForEvents,
      waitForNoInitializingShards = waitForNoInitializingShards,
      waitForNoRelocatingShards = waitForNoRelocatingShards,
      waitForNodes = waitForNodes,
      waitForStatus = waitForStatus
    )

    health(request)

  }

  def health(request: ClusterHealthRequest): ZioResponse[ClusterHealthResponse] =
    client.execute(request)

  /*
   * Returns a list of any cluster-level changes (e.g. create index, update mapping,
allocate or fail shard) which have not yet been executed.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-pending.html
   *
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Specify timeout for connection to master
   */
  def pendingTasks(
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None
  ): ZioResponse[ClusterPendingTasksResponse] = {
    val request =
      ClusterPendingTasksRequest(local = local, masterTimeout = masterTimeout)

    pendingTasks(request)

  }

  def pendingTasks(request: ClusterPendingTasksRequest): ZioResponse[ClusterPendingTasksResponse] =
    client.execute(request)

  /*
   * Updates the cluster settings.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-update-settings.html
   *
   * @param body body the body of the call
   * @param flatSettings Return settings in flat format (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param timeout Explicit operation timeout
   */
  def putSettings(
    body: JsonObject,
    flatSettings: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZioResponse[ClusterPutSettingsResponse] = {
    val request = ClusterPutSettingsRequest(
      body = body,
      flatSettings = flatSettings,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    putSettings(request)

  }

  def putSettings(request: ClusterPutSettingsRequest): ZioResponse[ClusterPutSettingsResponse] =
    client.execute(request)

  /*
   * Returns the information about configured remote clusters.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-remote-info.html
   *

   */
  def remoteInfo(
    ): ZioResponse[ClusterRemoteInfoResponse] = {
    val request = ClusterRemoteInfoRequest()

    remoteInfo(request)

  }

  def remoteInfo(request: ClusterRemoteInfoRequest): ZioResponse[ClusterRemoteInfoResponse] = client.execute(request)

  /*
   * Allows to manually change the allocation of individual shards in the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-reroute.html
   *
   * @param body body the body of the call
   * @param dryRun Simulate the operation only and return the resulting state
   * @param explain Return an explanation of why the commands can or cannot be executed
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param metric Limit the information returned to the specified metrics. Defaults to all but metadata
   * @param retryFailed Retries allocation of shards that are blocked due to too many subsequent allocation failures
   * @param timeout Explicit operation timeout
   */
  def reroute(
    body: Option[JsonObject] = None,
    dryRun: Option[Boolean] = None,
    explain: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    metric: Seq[String] = Nil,
    retryFailed: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZioResponse[ClusterRerouteResponse] = {
    val request = ClusterRerouteRequest(
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

  def reroute(request: ClusterRerouteRequest): ZioResponse[ClusterRerouteResponse] =
    client.execute(request)

  /*
   * Returns a comprehensive information about the state of the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-state.html
   *
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
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    flatSettings: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None,
    indices: Seq[String] = Nil,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    metric: Option[String] = None,
    waitForMetadataVersion: Option[Double] = None,
    waitForTimeout: Option[String] = None
  ): ZioResponse[ClusterStateResponse] = {
    val request = ClusterStateRequest(
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      flatSettings = flatSettings,
      ignoreUnavailable = ignoreUnavailable,
      indices = indices,
      local = local,
      masterTimeout = masterTimeout,
      metric = metric,
      waitForMetadataVersion = waitForMetadataVersion,
      waitForTimeout = waitForTimeout
    )

    state(request)

  }

  def state(request: ClusterStateRequest): ZioResponse[ClusterStateResponse] =
    client.execute(request)

  /*
   * Returns high-level overview of cluster statistics.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-stats.html
   *
   * @param flatSettings Return settings in flat format (default: false)
   * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param timeout Explicit operation timeout
   */
  def stats(
    flatSettings: Option[Boolean] = None,
    nodeId: Seq[String] = Nil,
    timeout: Option[String] = None
  ): ZioResponse[ClusterStatsResponse] = {
    val request = ClusterStatsRequest(flatSettings = flatSettings, nodeId = nodeId, timeout = timeout)

    stats(request)

  }

  def stats(request: ClusterStatsRequest): ZioResponse[ClusterStatsResponse] =
    client.execute(request)

}
