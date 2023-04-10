/*
 * Copyright 2019 Alberto Paro
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

package zio.elasticsearch.managers

import zio.elasticsearch._
import zio.elasticsearch.ZioResponse
import zio.elasticsearch.client.CatActionResolver
import zio.elasticsearch.requests.cat._
import zio.elasticsearch.responses.cat._

class CatManager(client: CatActionResolver) {

  /*
   * Shows information about currently configured aliases to indices including filter and routing infos.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-alias.html
   *
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param name A comma-separated list of alias names to return
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def aliases(
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    name: Chunk[String] = Chunk.empty,
    s: Chunk[String] = Chunk.empty,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatAliasesResponse] = {
    val request = CatAliasesRequest(
      format = format,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      name = name,
      s = s,
      v = v
    )

    aliases(request)

  }

  def aliases(request: CatAliasesRequest): ZIO[Any, FrameworkException, CatAliasesResponse] =
    httpService.execute(request)

  /*
   * Provides a snapshot of how many shards are allocated to each data node and how much disk space they are using.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-allocation.html
   *
   * @param bytes The unit in which to display byte values
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param nodeId A comma-separated list of node IDs or names to limit the returned information
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def allocation(
    bytes: Option[Bytes] = None,
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    nodeId: Chunk[String] = Chunk.empty,
    s: Chunk[String] = Chunk.empty,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatAllocationResponse] = {
    val request = CatAllocationRequest(
      bytes = bytes,
      format = format,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      nodeId = nodeId,
      s = s,
      v = v
    )

    allocation(request)

  }

  def allocation(
    request: CatAllocationRequest
  ): ZIO[Any, FrameworkException, CatAllocationResponse] = httpService.execute(request)

  /*
   * Provides quick access to the document count of the entire cluster, or individual indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-count.html
   *
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param indices A comma-separated list of index names to limit the returned information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def count(
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    indices: Chunk[String] = Chunk.empty,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatCountResponse] = {
    val request = CatCountRequest(
      format = format,
      h = h,
      help = help,
      indices = indices,
      local = local,
      masterTimeout = masterTimeout,
      s = s,
      v = v
    )

    count(request)

  }

  def count(request: CatCountRequest): ZIO[Any, FrameworkException, CatCountResponse] =
    httpService.execute(request)

  /*
   * Shows how much heap memory is currently being used by fielddata on every data node in the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-fielddata.html
   *
   * @param bytes The unit in which to display byte values
   * @param fields A comma-separated list of fields to return in the output
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def fielddata(
    bytes: Option[Bytes] = None,
    fields: Chunk[String] = Chunk.empty,
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatFielddataResponse] = {
    val request = CatFielddataRequest(
      bytes = bytes,
      fields = fields,
      format = format,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      s = s,
      v = v
    )

    fielddata(request)

  }

  def fielddata(
    request: CatFielddataRequest
  ): ZIO[Any, FrameworkException, CatFielddataResponse] = httpService.execute(request)

  /*
   * Returns a concise representation of the cluster health.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-health.html
   *
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param ts Set to false to disable timestamping
   * @param v Verbose mode. Display column headers
   */
  def health(
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    time: Option[Time] = None,
    ts: Boolean = true,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatHealthResponse] = {
    val request = CatHealthRequest(
      format = format,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      s = s,
      time = time,
      ts = ts,
      v = v
    )

    health(request)

  }

  def health(request: CatHealthRequest): ZIO[Any, FrameworkException, CatHealthResponse] =
    httpService.execute(request)

  /*
   * Returns help for the Cat APIs.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat.html
   *
   * @param help Return help information
   * @param s Comma-separated list of column names or column aliases to sort by
   */
  def help(
    helpB: Boolean = false,
    s: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, CatHelpResponse] =
    help(CatHelpRequest(help = helpB, s = s))

  def help(request: CatHelpRequest): ZIO[Any, FrameworkException, CatHelpResponse] =
    httpService.execute(request)

  /*
   * Returns information about indices: number of primaries and replicas, document counts, disk size, ...
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-indices.html
   *
   * @param bytes The unit in which to display byte values
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param health A health status ("green", "yellow", or "red" to filter only indices matching the specified health status
   * @param help Return help information
   * @param includeUnloadedSegments If set to true segment stats will include stats for segments that are not currently loaded into memory
   * @param indices A comma-separated list of index names to limit the returned information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param pri Set to true to return stats only for primary shards
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def indices(
    bytes: Option[Bytes] = None,
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    health: Option[ClusterHealthStatus] = None,
    help: Boolean = false,
    includeUnloadedSegments: Boolean = false,
    indices: Chunk[String] = Chunk.empty,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    pri: Boolean = false,
    s: Chunk[String] = Chunk.empty,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatIndicesResponse] = {
    val request = CatIndicesRequest(
      bytes = bytes,
      format = format,
      h = h,
      health = health,
      help = help,
      includeUnloadedSegments = includeUnloadedSegments,
      indices = indices,
      local = local,
      masterTimeout = masterTimeout,
      pri = pri,
      s = s,
      time = time,
      v = v
    )

    this.indices(request)

  }

  def indices(request: CatIndicesRequest): ZIO[Any, FrameworkException, CatIndicesResponse] =
    httpService.execute(request)

  /*
   * Returns information about the master node.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-master.html
   *
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def master(
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatMasterResponse] = {
    val request = CatMasterRequest(
      format = format,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      s = s,
      v = v
    )

    master(request)

  }

  def master(request: CatMasterRequest): ZIO[Any, FrameworkException, CatMasterResponse] =
    httpService.execute(request)

  /*
   * Returns information about custom node attributes.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-nodeattrs.html
   *
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def nodeattrs(
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatNodeattrsResponse] = {
    val request = CatNodeattrsRequest(
      format = format,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      s = s,
      v = v
    )

    nodeattrs(request)

  }

  def nodeattrs(
    request: CatNodeattrsRequest
  ): ZIO[Any, FrameworkException, CatNodeattrsResponse] = httpService.execute(request)

  /*
   * Returns basic statistics about performance of cluster nodes.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-nodes.html
   *
   * @param bytes The unit in which to display byte values
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param fullId Return the full node ID instead of the shortened version (default: false)
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def nodes(
    bytes: Option[Bytes] = None,
    format: Option[String] = None,
    fullId: Option[Boolean] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatNodesResponse] = {
    val request = CatNodesRequest(
      bytes = bytes,
      format = format,
      fullId = fullId,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      s = s,
      time = time,
      v = v
    )

    nodes(request)

  }

  def nodes(request: CatNodesRequest): ZIO[Any, FrameworkException, CatNodesResponse] =
    httpService.execute(request)

  /*
   * Returns a concise representation of the cluster pending tasks.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-pending-tasks.html
   *
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def pendingTasks(
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatPendingTasksResponse] = {
    val request = CatPendingTasksRequest(
      format = format,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      s = s,
      time = time,
      v = v
    )

    pendingTasks(request)

  }

  def pendingTasks(
    request: CatPendingTasksRequest
  ): ZIO[Any, FrameworkException, CatPendingTasksResponse] = httpService.execute(request)

  /*
   * Returns information about installed plugins across nodes node.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-plugins.html
   *
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def plugins(
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatPluginsResponse] = {
    val request = CatPluginsRequest(
      format = format,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      s = s,
      v = v
    )

    plugins(request)

  }

  def plugins(request: CatPluginsRequest): ZIO[Any, FrameworkException, CatPluginsResponse] =
    httpService.execute(request)

  /*
   * Returns information about index shard recoveries, both on-going completed.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-recovery.html
   *
   * @param activeOnly If `true`, the response only includes ongoing shard recoveries
   * @param bytes The unit in which to display byte values
   * @param detailed If `true`, the response includes detailed information about shard recoveries
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param index Comma-separated list or wildcard expression of index names to limit the returned information
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def recovery(
    activeOnly: Boolean = false,
    bytes: Option[Bytes] = None,
    detailed: Boolean = false,
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    index: Chunk[String] = Chunk.empty,
    masterTimeout: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatRecoveryResponse] = {
    val request = CatRecoveryRequest(
      activeOnly = activeOnly,
      bytes = bytes,
      detailed = detailed,
      format = format,
      h = h,
      help = help,
      index = index,
      masterTimeout = masterTimeout,
      s = s,
      time = time,
      v = v
    )

    recovery(request)

  }

  def recovery(request: CatRecoveryRequest): ZIO[Any, FrameworkException, CatRecoveryResponse] =
    httpService.execute(request)

  /*
   * Returns information about snapshot repositories registered in the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-repositories.html
   *
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def repositories(
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    local: Boolean = false,
    masterTimeout: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatRepositoriesResponse] = {
    val request = CatRepositoriesRequest(
      format = format,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      s = s,
      v = v
    )

    repositories(request)

  }

  def repositories(
    request: CatRepositoriesRequest
  ): ZIO[Any, FrameworkException, CatRepositoriesResponse] = httpService.execute(request)

  /*
   * Provides low-level information about the segments in the shards of an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-segments.html
   *
   * @param bytes The unit in which to display byte values
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param indices A comma-separated list of index names to limit the returned information
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def segments(
    bytes: Option[Bytes] = None,
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    indices: Chunk[String] = Chunk.empty,
    s: Chunk[String] = Chunk.empty,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatSegmentsResponse] = {
    val request = CatSegmentsRequest(
      bytes = bytes,
      format = format,
      h = h,
      help = help,
      indices = indices,
      s = s,
      v = v
    )

    segments(request)

  }

  def segments(request: CatSegmentsRequest): ZIO[Any, FrameworkException, CatSegmentsResponse] =
    httpService.execute(request)

  /*
   * Provides a detailed view of shard allocation on nodes.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-shards.html
   *
   * @param bytes The unit in which to display byte values
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param indices A comma-separated list of index names to limit the returned information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def shards(
    bytes: Option[Bytes] = None,
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    indices: Chunk[String] = Chunk.empty,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatShardsResponse] = {
    val request = CatShardsRequest(
      bytes = bytes,
      format = format,
      h = h,
      help = help,
      indices = indices,
      local = local,
      masterTimeout = masterTimeout,
      s = s,
      time = time,
      v = v
    )

    shards(request)

  }

  def shards(request: CatShardsRequest): ZIO[Any, FrameworkException, CatShardsResponse] =
    httpService.execute(request)

  /*
   * Returns all snapshots in a specific repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-snapshots.html
   *
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param ignoreUnavailable Set to true to ignore unavailable snapshots
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param repository Name of repository from which to fetch the snapshot information
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def snapshots(
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    ignoreUnavailable: Boolean = false,
    masterTimeout: Option[String] = None,
    repository: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatSnapshotsResponse] = {
    val request = CatSnapshotsRequest(
      format = format,
      h = h,
      help = help,
      ignoreUnavailable = ignoreUnavailable,
      masterTimeout = masterTimeout,
      repository = repository,
      s = s,
      time = time,
      v = v
    )

    snapshots(request)

  }

  def snapshots(
    request: CatSnapshotsRequest
  ): ZIO[Any, FrameworkException, CatSnapshotsResponse] = httpService.execute(request)

  /*
   * Returns information about the tasks currently executing on one or more nodes in the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
   *
   * @param actions A comma-separated list of actions that should be returned. Leave empty to return all.
   * @param detailed Return detailed task information (default: false)
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param parentTask Return tasks with specified parent task id. Set to -1 to return all.
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def tasks(
    actions: Chunk[String] = Chunk.empty,
    detailed: Option[Boolean] = None,
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    nodeId: Chunk[String] = Chunk.empty,
    parentTask: Option[Double] = None,
    s: Chunk[String] = Chunk.empty,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatTasksResponse] = {
    val request = CatTasksRequest(
      actions = actions,
      detailed = detailed,
      format = format,
      h = h,
      help = help,
      nodeId = nodeId,
      parentTask = parentTask,
      s = s,
      time = time,
      v = v
    )

    tasks(request)

  }

  def tasks(request: CatTasksRequest): ZIO[Any, FrameworkException, CatTasksResponse] =
    httpService.execute(request)

  /*
   * Returns information about existing templates.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-templates.html
   *
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param name A pattern that returned template names must match
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def templates(
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    name: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatTemplatesResponse] = {
    val request = CatTemplatesRequest(
      format = format,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      name = name,
      s = s,
      v = v
    )

    templates(request)

  }

  def templates(
    request: CatTemplatesRequest
  ): ZIO[Any, FrameworkException, CatTemplatesResponse] = httpService.execute(request)

  /*
   * Returns cluster-wide thread pool statistics per node.
By default the active, queue and rejected statistics are returned for all thread pools.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-thread-pool.html
   *
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param size The multiplier in which to display values
   * @param threadPoolPatterns A comma-separated list of regular-expressions to filter the thread pools in the output
   * @param v Verbose mode. Display column headers
   */
  def threadPool(
    format: Option[String] = None,
    h: Chunk[String] = Chunk.empty,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Chunk[String] = Chunk.empty,
    size: Option[Size] = None,
    threadPoolPatterns: Chunk[String] = Chunk.empty,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CatThreadPoolResponse] = {
    val request = CatThreadPoolRequest(
      format = format,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      s = s,
      size = size,
      threadPoolPatterns = threadPoolPatterns,
      v = v
    )

    threadPool(request)

  }

  def threadPool(
    request: CatThreadPoolRequest
  ): ZIO[Any, FrameworkException, CatThreadPoolResponse] = httpService.execute(request)

}
