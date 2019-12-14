/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.cluster

import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-state.html
 *
 * @param metric Limit the information returned to the specified metrics
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param masterTimeout Specify timeout for connection to master
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param flatSettings Return settings in flat format (default: false)
 */

@JsonCodec
final case class ClusterStateResponse(
  @JsonKey("cluster_name") clusterName: String = "",
  version: Int = 0,
  @JsonKey("master_node") masterNode: String = "",
  blocks: Option[Blocks] = None,
  nodes: Map[String, Node] = Map.empty[String, Node],
  metadata: Metadata = Metadata(),
  @JsonKey("routing_table") routingTable: Option[RoutingTable] = None,
  @JsonKey("routing_nodes") routingNodes: Option[RoutingNodes] = None
)
