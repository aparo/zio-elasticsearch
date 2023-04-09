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

package zio.elasticsearch.cat

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._

import zio.elasticsearch.cat.aliases.AliasesRequest
import zio.elasticsearch.cat.aliases.AliasesResponse
import zio.elasticsearch.cat.allocation.AllocationRequest
import zio.elasticsearch.cat.allocation.AllocationResponse
import zio.elasticsearch.cat.component_templates.ComponentTemplatesRequest
import zio.elasticsearch.cat.component_templates.ComponentTemplatesResponse
import zio.elasticsearch.cat.count.CountRequest
import zio.elasticsearch.cat.count.CountResponse
import zio.elasticsearch.cat.fielddata.FielddataRequest
import zio.elasticsearch.cat.fielddata.FielddataResponse
import zio.elasticsearch.cat.health.HealthRequest
import zio.elasticsearch.cat.health.HealthResponse
import zio.elasticsearch.cat.help.HelpRequest
import zio.elasticsearch.cat.help.HelpResponse
import zio.elasticsearch.cat.indices.IndicesRequest
import zio.elasticsearch.cat.indices.IndicesResponse
import zio.elasticsearch.cat.master.MasterRequest
import zio.elasticsearch.cat.master.MasterResponse
import zio.elasticsearch.cat.ml_data_frame_analytics.MlDataFrameAnalyticsRequest
import zio.elasticsearch.cat.ml_data_frame_analytics.MlDataFrameAnalyticsResponse
import zio.elasticsearch.cat.ml_datafeeds.MlDatafeedsRequest
import zio.elasticsearch.cat.ml_datafeeds.MlDatafeedsResponse
import zio.elasticsearch.cat.ml_jobs.MlJobsRequest
import zio.elasticsearch.cat.ml_jobs.MlJobsResponse
import zio.elasticsearch.cat.ml_trained_models.MlTrainedModelsRequest
import zio.elasticsearch.cat.ml_trained_models.MlTrainedModelsResponse
import zio.elasticsearch.cat.nodeattrs.NodeattrsRequest
import zio.elasticsearch.cat.nodeattrs.NodeattrsResponse
import zio.elasticsearch.cat.nodes.NodesRequest
import zio.elasticsearch.cat.nodes.NodesResponse
import zio.elasticsearch.cat.pending_tasks.PendingTasksRequest
import zio.elasticsearch.cat.pending_tasks.PendingTasksResponse
import zio.elasticsearch.cat.plugins.PluginsRequest
import zio.elasticsearch.cat.plugins.PluginsResponse
import zio.elasticsearch.cat.recovery.RecoveryRequest
import zio.elasticsearch.cat.recovery.RecoveryResponse
import zio.elasticsearch.cat.repositories.RepositoriesRequest
import zio.elasticsearch.cat.repositories.RepositoriesResponse
import zio.elasticsearch.cat.segments.SegmentsRequest
import zio.elasticsearch.cat.segments.SegmentsResponse
import zio.elasticsearch.cat.shards.ShardsRequest
import zio.elasticsearch.cat.shards.ShardsResponse
import zio.elasticsearch.cat.snapshots.SnapshotsRequest
import zio.elasticsearch.cat.snapshots.SnapshotsResponse
import zio.elasticsearch.cat.tasks.TasksRequest
import zio.elasticsearch.cat.tasks.TasksResponse
import zio.elasticsearch.cat.templates.TemplatesRequest
import zio.elasticsearch.cat.templates.TemplatesResponse
import zio.elasticsearch.cat.thread_pool.ThreadPoolRequest
import zio.elasticsearch.cat.thread_pool.ThreadPoolResponse
import zio.elasticsearch.cat.transforms.TransformsRequest
import zio.elasticsearch.cat.transforms.TransformsResponse

object CatManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, CatManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new CatManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait CatManager {
  def httpService: ElasticSearchHttpService

  /*
   * Shows information about currently configured aliases to indices including filter and routing infos.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-alias.html
   *
   * @param masterTimeout Period to wait for a connection to the master node.
   * @server_default 30s

   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param name A comma-separated list of alias names to return
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def aliases(
    masterTimeout: Option[String] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    local: Option[Boolean] = None,
    name: Seq[String] = Nil,
    s: Seq[String] = Nil,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, AliasesResponse] = {
    val request = AliasesRequest(
      masterTimeout = masterTimeout,
      expandWildcards = expandWildcards,
      format = format,
      h = h,
      help = help,
      local = local,
      name = name,
      s = s,
      v = v
    )

    aliases(request)

  }

  def aliases(
    request: AliasesRequest
  ): ZIO[Any, FrameworkException, AliasesResponse] =
    httpService.execute[Json, AliasesResponse](request)

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
    h: Seq[String] = Nil,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    nodeId: Seq[String] = Nil,
    s: Seq[String] = Nil,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, AllocationResponse] = {
    val request = AllocationRequest(
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
    request: AllocationRequest
  ): ZIO[Any, FrameworkException, AllocationResponse] =
    httpService.execute[Json, AllocationResponse](request)

  /*
   * Returns information about existing component_templates templates.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-compoentn-templates.html
   *
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param name A pattern that returned component template names must match
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def componentTemplates(
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    name: Option[String] = None,
    s: Seq[String] = Nil,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, ComponentTemplatesResponse] = {
    val request = ComponentTemplatesRequest(
      format = format,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      name = name,
      s = s,
      v = v
    )

    componentTemplates(request)

  }

  def componentTemplates(
    request: ComponentTemplatesRequest
  ): ZIO[Any, FrameworkException, ComponentTemplatesResponse] =
    httpService.execute[Json, ComponentTemplatesResponse](request)

  /*
   * Provides quick access to the document count of the entire cluster, or individual indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-count.html
   *
   * @param index

   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param masterTimeout Period to wait for a connection to the master node.
   * @server_default 30s

   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param indices A comma-separated list of index names to limit the returned information
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def count(
    index: Chunk[String],
    local: Boolean,
    masterTimeout: Option[String] = None,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    indices: Seq[String] = Nil,
    s: Seq[String] = Nil,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, CountResponse] = {
    val request = CountRequest(
      index = index,
      local = local,
      masterTimeout = masterTimeout,
      format = format,
      h = h,
      help = help,
      indices = indices,
      s = s,
      v = v
    )

    count(request)

  }

  def count(
    request: CountRequest
  ): ZIO[Any, FrameworkException, CountResponse] =
    httpService.execute[Json, CountResponse](request)

  /*
   * Shows how much heap memory is currently being used by fielddata on every data node in the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-fielddata.html
   *
   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param masterTimeout Period to wait for a connection to the master node.
   * @server_default 30s

   * @param bytes The unit in which to display byte values
   * @param fields A comma-separated list of fields to return in the output
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def fielddata(
    local: Boolean,
    masterTimeout: Option[String] = None,
    bytes: Option[Bytes] = None,
    fields: Seq[String] = Nil,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    s: Seq[String] = Nil,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, FielddataResponse] = {
    val request = FielddataRequest(
      local = local,
      masterTimeout = masterTimeout,
      bytes = bytes,
      fields = fields,
      format = format,
      h = h,
      help = help,
      s = s,
      v = v
    )

    fielddata(request)

  }

  def fielddata(
    request: FielddataRequest
  ): ZIO[Any, FrameworkException, FielddataResponse] =
    httpService.execute[Json, FielddataResponse](request)

  /*
   * Returns a concise representation of the cluster health.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-health.html
   *
   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param masterTimeout Period to wait for a connection to the master node.
   * @server_default 30s

   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param ts Set to false to disable timestamping
   * @param v Verbose mode. Display column headers
   */
  def health(
    local: Boolean,
    masterTimeout: Option[String] = None,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    ts: Boolean = true,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, HealthResponse] = {
    val request = HealthRequest(
      local = local,
      masterTimeout = masterTimeout,
      format = format,
      h = h,
      help = help,
      s = s,
      time = time,
      ts = ts,
      v = v
    )

    health(request)

  }

  def health(
    request: HealthRequest
  ): ZIO[Any, FrameworkException, HealthResponse] =
    httpService.execute[Json, HealthResponse](request)

  /*
   * Returns help for the Cat APIs.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat.html
   *
   * @param format Specifies the format to return the columnar data in, can be set to
   * `text`, `json`, `cbor`, `yaml`, or `smile`.
   * @server_default text

   * @param h List of columns to appear in the response. Supports simple wildcards.

   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param masterTimeout Period to wait for a connection to the master node.
   * @server_default 30s

   * @param v When set to `true` will enable verbose output.
   * @server_default false

   * @param help Return help information
   * @param s Comma-separated list of column names or column aliases to sort by
   */
  def help(
    format: String,
    h: Names,
    local: Boolean,
    masterTimeout: Option[String] = None,
    v: Boolean,
//    help: Boolean = false,
    s: Seq[String] = Nil
  ): ZIO[Any, FrameworkException, HelpResponse] = {
    val request = HelpRequest(
      format = format,
      h = h,
      local = local,
      masterTimeout = masterTimeout,
      v = v,
//      help = help,
      s = s
    )

    help(request)

  }

  def help(request: HelpRequest): ZIO[Any, FrameworkException, HelpResponse] =
    httpService.execute[Json, HelpResponse](request)

  /*
   * Returns information about indices: number of primaries and replicas, document counts, disk size, ...
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-indices.html
   *
   * @param index

   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param bytes The unit in which to display byte values
   * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param health A health status ("green", "yellow", or "red" to filter only indices matching the specified health status
   * @param help Return help information
   * @param includeUnloadedSegments If set to true segment stats will include stats for segments that are not currently loaded into memory
   * @param indices A comma-separated list of index names to limit the returned information
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param pri Set to true to return stats only for primary shards
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def listIndices(
    index: Chunk[String],
    local: Boolean,
    bytes: Option[Bytes] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    health: Option[Health] = None,
    help: Boolean = false,
    includeUnloadedSegments: Boolean = false,
    indices: Seq[String] = Nil,
    masterTimeout: Option[String] = None,
    pri: Boolean = false,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, IndicesResponse] = {
    val request = IndicesRequest(
      index = index,
      local = local,
      bytes = bytes,
      expandWildcards = expandWildcards,
      format = format,
      h = h,
      health = health,
      help = help,
      includeUnloadedSegments = includeUnloadedSegments,
      indices = indices,
      masterTimeout = masterTimeout,
      pri = pri,
      s = s,
      time = time,
      v = v
    )

    listIndices(request)

  }

  def listIndices(
    request: IndicesRequest
  ): ZIO[Any, FrameworkException, IndicesResponse] =
    httpService.execute[Json, IndicesResponse](request)

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
    h: Seq[String] = Nil,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Seq[String] = Nil,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, MasterResponse] = {
    val request = MasterRequest(
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

  def master(
    request: MasterRequest
  ): ZIO[Any, FrameworkException, MasterResponse] =
    httpService.execute[Json, MasterResponse](request)

  /*
   * Gets configuration and usage information about data frame analytics jobs.
   * For more info refers to http://www.elastic.co/guide/en/elasticsearch/reference/current/cat-dfanalytics.html
   *
   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param masterTimeout Period to wait for a connection to the master node.
   * @server_default 30s

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no configs. (This includes `_all` string or when no configs have been specified)
   * @param bytes The unit in which to display byte values
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param id The ID of the data frame analytics to fetch
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def mlDataFrameAnalytics(
    local: Boolean,
    masterTimeout: Option[String] = None,
    allowNoMatch: Option[Boolean] = None,
    bytes: Option[Bytes] = None,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    id: Option[String] = None,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, MlDataFrameAnalyticsResponse] = {
    val request = MlDataFrameAnalyticsRequest(
      local = local,
      masterTimeout = masterTimeout,
      allowNoMatch = allowNoMatch,
      bytes = bytes,
      format = format,
      h = h,
      help = help,
      id = id,
      s = s,
      time = time,
      v = v
    )

    mlDataFrameAnalytics(request)

  }

  def mlDataFrameAnalytics(
    request: MlDataFrameAnalyticsRequest
  ): ZIO[Any, FrameworkException, MlDataFrameAnalyticsResponse] =
    httpService.execute[Json, MlDataFrameAnalyticsResponse](request)

  /*
   * Gets configuration and usage information about datafeeds.
   * For more info refers to http://www.elastic.co/guide/en/elasticsearch/reference/current/cat-datafeeds.html
   *
   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param masterTimeout Period to wait for a connection to the master node.
   * @server_default 30s

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no datafeeds. (This includes `_all` string or when no datafeeds have been specified)
   * @param datafeedId The ID of the datafeeds stats to fetch
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def mlDatafeeds(
    local: Boolean,
    masterTimeout: Option[String] = None,
    allowNoMatch: Option[Boolean] = None,
    datafeedId: Option[String] = None,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, MlDatafeedsResponse] = {
    val request = MlDatafeedsRequest(
      local = local,
      masterTimeout = masterTimeout,
      allowNoMatch = allowNoMatch,
      datafeedId = datafeedId,
      format = format,
      h = h,
      help = help,
      s = s,
      time = time,
      v = v
    )

    mlDatafeeds(request)

  }

  def mlDatafeeds(
    request: MlDatafeedsRequest
  ): ZIO[Any, FrameworkException, MlDatafeedsResponse] =
    httpService.execute[Json, MlDatafeedsResponse](request)

  /*
   * Gets configuration and usage information about anomaly detection jobs.
   * For more info refers to http://www.elastic.co/guide/en/elasticsearch/reference/current/cat-anomaly-detectors.html
   *
   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param masterTimeout Period to wait for a connection to the master node.
   * @server_default 30s

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no jobs. (This includes `_all` string or when no jobs have been specified)
   * @param bytes The unit in which to display byte values
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param jobId The ID of the jobs stats to fetch
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def mlJobs(
    local: Boolean,
    masterTimeout: Option[String] = None,
    allowNoMatch: Option[Boolean] = None,
    bytes: Option[Bytes] = None,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    jobId: Option[String] = None,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, MlJobsResponse] = {
    val request = MlJobsRequest(
      local = local,
      masterTimeout = masterTimeout,
      allowNoMatch = allowNoMatch,
      bytes = bytes,
      format = format,
      h = h,
      help = help,
      jobId = jobId,
      s = s,
      time = time,
      v = v
    )

    mlJobs(request)

  }

  def mlJobs(
    request: MlJobsRequest
  ): ZIO[Any, FrameworkException, MlJobsResponse] =
    httpService.execute[Json, MlJobsResponse](request)

  /*
   * Gets configuration and usage information about inference trained models.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/cat-trained-model.html
   *
   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param masterTimeout Period to wait for a connection to the master node.
   * @server_default 30s

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no trained models. (This includes `_all` string or when no trained models have been specified)
   * @param bytes The unit in which to display byte values
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param from skips a number of trained models
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param modelId The ID of the trained models stats to fetch
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param size specifies a max number of trained models to get
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def mlTrainedModels(
    local: Boolean,
    masterTimeout: Option[String] = None,
    allowNoMatch: Boolean = true,
    bytes: Option[Bytes] = None,
    format: Option[String] = None,
    from: Int = 0,
    h: Seq[String] = Nil,
    help: Boolean = false,
    modelId: Option[String] = None,
    s: Seq[String] = Nil,
    size: Int = 100,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, MlTrainedModelsResponse] = {
    val request = MlTrainedModelsRequest(
      local = local,
      masterTimeout = masterTimeout,
      allowNoMatch = allowNoMatch,
      bytes = bytes,
      format = format,
      from = from,
      h = h,
      help = help,
      modelId = modelId,
      s = s,
      size = size,
      time = time,
      v = v
    )

    mlTrainedModels(request)

  }

  def mlTrainedModels(
    request: MlTrainedModelsRequest
  ): ZIO[Any, FrameworkException, MlTrainedModelsResponse] =
    httpService.execute[Json, MlTrainedModelsResponse](request)

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
    h: Seq[String] = Nil,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Seq[String] = Nil,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, NodeattrsResponse] = {
    val request = NodeattrsRequest(
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
    request: NodeattrsRequest
  ): ZIO[Any, FrameworkException, NodeattrsResponse] =
    httpService.execute[Json, NodeattrsResponse](request)

  /*
   * Returns basic statistics about performance of cluster nodes.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-nodes.html
   *
   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param bytes The unit in which to display byte values
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param fullId Return the full node ID instead of the shortened version (default: false)
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param includeUnloadedSegments If set to true segment stats will include stats for segments that are not currently loaded into memory
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def nodes(
    local: Boolean,
    bytes: Option[Bytes] = None,
    format: Option[String] = None,
    fullId: Option[Boolean] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    includeUnloadedSegments: Boolean = false,
    masterTimeout: Option[String] = None,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, NodesResponse] = {
    val request = NodesRequest(
      local = local,
      bytes = bytes,
      format = format,
      fullId = fullId,
      h = h,
      help = help,
      includeUnloadedSegments = includeUnloadedSegments,
      masterTimeout = masterTimeout,
      s = s,
      time = time,
      v = v
    )

    nodes(request)

  }

  def nodes(
    request: NodesRequest
  ): ZIO[Any, FrameworkException, NodesResponse] =
    httpService.execute[Json, NodesResponse](request)

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
    h: Seq[String] = Nil,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, PendingTasksResponse] = {
    val request = PendingTasksRequest(
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
    request: PendingTasksRequest
  ): ZIO[Any, FrameworkException, PendingTasksResponse] =
    httpService.execute[Json, PendingTasksResponse](request)

  /*
   * Returns information about installed plugins across nodes node.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-plugins.html
   *
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param includeBootstrap Include bootstrap plugins in the response
   * @param local Return local information, do not retrieve the state from master node (default: false)
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def plugins(
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    includeBootstrap: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Seq[String] = Nil,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, PluginsResponse] = {
    val request = PluginsRequest(
      format = format,
      h = h,
      help = help,
      includeBootstrap = includeBootstrap,
      local = local,
      masterTimeout = masterTimeout,
      s = s,
      v = v
    )

    plugins(request)

  }

  def plugins(
    request: PluginsRequest
  ): ZIO[Any, FrameworkException, PluginsResponse] =
    httpService.execute[Json, PluginsResponse](request)

  /*
   * Returns information about index shard recoveries, both on-going completed.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-recovery.html
   *
   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param masterTimeout Period to wait for a connection to the master node.
   * @server_default 30s

   * @param activeOnly If `true`, the response only includes ongoing shard recoveries
   * @param bytes The unit in which to display byte values
   * @param detailed If `true`, the response includes detailed information about shard recoveries
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param index Comma-separated list or wildcard expression of index names to limit the returned information
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def recovery(
    local: Boolean,
    masterTimeout: Option[String] = None,
    activeOnly: Boolean = false,
    bytes: Option[Bytes] = None,
    detailed: Boolean = false,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    index: Seq[String] = Nil,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, RecoveryResponse] = {
    val request = RecoveryRequest(
      local = local,
      masterTimeout = masterTimeout,
      activeOnly = activeOnly,
      bytes = bytes,
      detailed = detailed,
      format = format,
      h = h,
      help = help,
      index = index,
      s = s,
      time = time,
      v = v
    )

    recovery(request)

  }

  def recovery(
    request: RecoveryRequest
  ): ZIO[Any, FrameworkException, RecoveryResponse] =
    httpService.execute[Json, RecoveryResponse](request)

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
    h: Seq[String] = Nil,
    help: Boolean = false,
    local: Boolean = false,
    masterTimeout: Option[String] = None,
    s: Seq[String] = Nil,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, RepositoriesResponse] = {
    val request = RepositoriesRequest(
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
    request: RepositoriesRequest
  ): ZIO[Any, FrameworkException, RepositoriesResponse] =
    httpService.execute[Json, RepositoriesResponse](request)

  /*
   * Provides low-level information about the segments in the shards of an index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-segments.html
   *
   * @param index

   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param masterTimeout Period to wait for a connection to the master node.
   * @server_default 30s

   * @param bytes The unit in which to display byte values
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param indices A comma-separated list of index names to limit the returned information
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param v Verbose mode. Display column headers
   */
  def segments(
    index: Chunk[String],
    local: Boolean,
    masterTimeout: Option[String] = None,
    bytes: Option[Bytes] = None,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    indices: Seq[String] = Nil,
    s: Seq[String] = Nil,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, SegmentsResponse] = {
    val request = SegmentsRequest(
      index = index,
      local = local,
      masterTimeout = masterTimeout,
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

  def segments(
    request: SegmentsRequest
  ): ZIO[Any, FrameworkException, SegmentsResponse] =
    httpService.execute[Json, SegmentsResponse](request)

  /*
   * Provides a detailed view of shard allocation on nodes.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-shards.html
   *
   * @param index

   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param bytes The unit in which to display byte values
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param indices A comma-separated list of index names to limit the returned information
   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def shards(
    index: Chunk[String],
    local: Boolean,
    bytes: Option[Bytes] = None,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    indices: Seq[String] = Nil,
    masterTimeout: Option[String] = None,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, ShardsResponse] = {
    val request = ShardsRequest(
      index = index,
      local = local,
      bytes = bytes,
      format = format,
      h = h,
      help = help,
      indices = indices,
      masterTimeout = masterTimeout,
      s = s,
      time = time,
      v = v
    )

    shards(request)

  }

  def shards(
    request: ShardsRequest
  ): ZIO[Any, FrameworkException, ShardsResponse] =
    httpService.execute[Json, ShardsResponse](request)

  /*
   * Returns all snapshots in a specific repository.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-snapshots.html
   *
   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

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
    local: Boolean,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    ignoreUnavailable: Boolean = false,
    masterTimeout: Option[String] = None,
    repository: Option[String] = None,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, SnapshotsResponse] = {
    val request = SnapshotsRequest(
      local = local,
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
    request: SnapshotsRequest
  ): ZIO[Any, FrameworkException, SnapshotsResponse] =
    httpService.execute[Json, SnapshotsResponse](request)

  /*
   * Returns information about the tasks currently executing on one or more nodes in the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
   *
   * @param nodeId

   * @param parentTask

   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param masterTimeout Period to wait for a connection to the master node.
   * @server_default 30s

   * @param actions A comma-separated list of actions that should be returned. Leave empty to return all.
   * @param detailed Return detailed task information (default: false)
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param nodes A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param parentTaskId Return tasks with specified parent task id (node_id:task_number). Set to -1 to return all.
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def tasks(
    nodeId: Chunk[String],
    parentTask: Long,
    local: Boolean,
    masterTimeout: Option[String] = None,
    actions: Seq[String] = Nil,
    detailed: Option[Boolean] = None,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    nodes: Seq[String] = Nil,
    parentTaskId: Option[String] = None,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, TasksResponse] = {
    val request = TasksRequest(
      nodeId = nodeId,
      parentTask = parentTask,
      local = local,
      masterTimeout = masterTimeout,
      actions = actions,
      detailed = detailed,
      format = format,
      h = h,
      help = help,
      nodes = nodes,
      parentTaskId = parentTaskId,
      s = s,
      time = time,
      v = v
    )

    tasks(request)

  }

  def tasks(
    request: TasksRequest
  ): ZIO[Any, FrameworkException, TasksResponse] =
    httpService.execute[Json, TasksResponse](request)

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
    h: Seq[String] = Nil,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    name: Option[String] = None,
    s: Seq[String] = Nil,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, TemplatesResponse] = {
    val request = TemplatesRequest(
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
    request: TemplatesRequest
  ): ZIO[Any, FrameworkException, TemplatesResponse] =
    httpService.execute[Json, TemplatesResponse](request)

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
   * @param threadPoolPatterns A comma-separated list of regular-expressions to filter the thread pools in the output
   * @param time The unit in which to display time values
   * @param v Verbose mode. Display column headers
   */
  def threadPool(
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    local: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    s: Seq[String] = Nil,
    threadPoolPatterns: Seq[String] = Nil,
    time: Option[Time] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, ThreadPoolResponse] = {
    val request = ThreadPoolRequest(
      format = format,
      h = h,
      help = help,
      local = local,
      masterTimeout = masterTimeout,
      s = s,
      threadPoolPatterns = threadPoolPatterns,
      time = time,
      v = v
    )

    threadPool(request)

  }

  def threadPool(
    request: ThreadPoolRequest
  ): ZIO[Any, FrameworkException, ThreadPoolResponse] =
    httpService.execute[Json, ThreadPoolResponse](request)

  /*
   * Gets configuration and usage information about transforms.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/cat-transforms.html
   *
   * @param local If `true`, the request computes the list of selected nodes from the
   * local cluster state. If `false` the list of selected nodes are computed
   * from the cluster state of the master node. In both cases the coordinating
   * node will send requests for further information to each selected node.
   * @server_default false

   * @param masterTimeout Period to wait for a connection to the master node.
   * @server_default 30s

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no transforms. (This includes `_all` string or when no transforms have been specified)
   * @param format a short version of the Accept header, e.g. json, yaml
   * @param from skips a number of transform configs, defaults to 0
   * @param h Comma-separated list of column names to display
   * @param help Return help information
   * @param s Comma-separated list of column names or column aliases to sort by
   * @param size specifies a max number of transforms to get, defaults to 100
   * @param time The unit in which to display time values
   * @param transformId The id of the transform for which to get stats. '_all' or '*' implies all transforms
   * @param v Verbose mode. Display column headers
   */
  def transforms(
    local: Boolean,
    masterTimeout: Option[String] = None,
    allowNoMatch: Option[Boolean] = None,
    format: Option[String] = None,
    from: Option[Int] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    s: Seq[String] = Nil,
    size: Option[Int] = None,
    time: Option[Time] = None,
    transformId: Option[String] = None,
    v: Boolean = false
  ): ZIO[Any, FrameworkException, TransformsResponse] = {
    val request = TransformsRequest(
      local = local,
      masterTimeout = masterTimeout,
      allowNoMatch = allowNoMatch,
      format = format,
      from = from,
      h = h,
      help = help,
      s = s,
      size = size,
      time = time,
      transformId = transformId,
      v = v
    )

    transforms(request)

  }

  def transforms(
    request: TransformsRequest
  ): ZIO[Any, FrameworkException, TransformsResponse] =
    httpService.execute[Json, TransformsResponse](request)

}
