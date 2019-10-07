/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.managers

import elasticsearch.requests.nodes.{ NodesHotThreadsRequest, NodesInfoRequest, NodesStatsRequest }
import elasticsearch.responses.nodes.{ NodesHotThreadsResponse, NodesInfoResponse, NodesStatsResponse }
import elasticsearch._
import elasticsearch.ElasticSearch
import elasticsearch.ZioResponse

class NodesManager(client: ElasticSearch) {

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-hot-threads.html
   *
   * @param nodeIds A list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param snapshots Number of samples of thread stacktrace (default: 10)
   * @param interval The interval for the second sampling of threads
   * @param `type` The type to sample (default: cpu)
   * @param ignoreIdleThreads Don't show threads that are in known-idle places, such as waiting on a socket select or pulling from an empty task queue (default: true)
   * @param threads Specify the number of threads to provide information for (default: 3)
   * @param timeout Explicit operation timeout
   */
  def hotThreads(
    nodeId: Seq[String] = Nil,
    snapshots: Double = 10,
    interval: Option[String] = None,
    `type`: Type = Type.Cpu,
    ignoreIdleThreads: Boolean = true,
    threads: Double = 3,
    timeout: Option[String] = None
  ): ZioResponse[NodesHotThreadsResponse] = {
    val request = NodesHotThreadsRequest(
      nodeId = nodeId,
      snapshots = snapshots,
      interval = interval,
      `type` = `type`,
      ignoreIdleThreads = ignoreIdleThreads,
      threads = threads,
      timeout = timeout
    )

    hotThreads(request)

  }

  def hotThreads(
    request: NodesHotThreadsRequest
  ): ZioResponse[NodesHotThreadsResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-info.html
   *
   * @param nodeIds A list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param metric As list of metrics you wish returned. Leave empty to return all.
   * @param flatSettings Return settings in flat format (default: false)
   * @param timeout Explicit operation timeout
   */
  def info(
    nodeIds: Seq[String] = Nil,
    metric: Seq[String] = Nil,
    flatSettings: Boolean = false,
    timeout: Option[String] = None
  ): ZioResponse[NodesInfoResponse] = {
    val request = NodesInfoRequest(
      nodeIds = nodeIds,
      metric = metric,
      flatSettings = flatSettings,
      timeout = timeout
    )

    info(request)

  }

  def info(
    request: NodesInfoRequest
  ): ZioResponse[NodesInfoResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-stats.html
   *
   * @param nodeId A list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param metric Limit the information returned to the specified metrics
   * @param indexMetric Limit the information returned for `indices` metric to the specific index metrics. Isn't used if `indices` (or `all`) metric isn't specified.
   * @param fielddataFields A list of fields for `fielddata` index metric (supports wildcards)
   * @param groups A list of search groups for `search` index metric
   * @param completionFields A list of fields for `fielddata` and `suggest` index metric (supports wildcards)
   * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
   * @param fields A list of fields for `fielddata` and `completion` index metric (supports wildcards)
   * @param types A list of document types for the `indexing` index metric
   * @param timeout Explicit operation timeout
   * @param level Return indices stats aggregated at index, node or shard level
   */
  def stats(
    nodeId: Seq[String] = Nil,
    metric: Option[String] = None,
    indexMetric: Option[String] = None,
    fielddataFields: Seq[String] = Nil,
    groups: Seq[String] = Nil,
    completionFields: Seq[String] = Nil,
    includeSegmentFileSizes: Boolean = false,
    fields: Seq[String] = Nil,
    types: Seq[String] = Nil,
    timeout: Option[String] = None,
    level: Level = Level.node
  ): ZioResponse[NodesStatsResponse] = {
    val request = NodesStatsRequest(
      nodeId = nodeId,
      metric = metric,
      indexMetric = indexMetric,
      fielddataFields = fielddataFields,
      groups = groups,
      completionFields = completionFields,
      includeSegmentFileSizes = includeSegmentFileSizes,
      fields = fields,
      types = types,
      timeout = timeout,
      level = level
    )

    stats(request)

  }

  def stats(
    request: NodesStatsRequest
  ): ZioResponse[NodesStatsResponse] =
    client.execute(request)

}
