/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.nodes

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-info.html
 *
 * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param metric A comma-separated list of metrics you wish returned. Leave empty to return all.
 * @param flatSettings Return settings in flat format (default: false)
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class NodesInfoResponse() {}
