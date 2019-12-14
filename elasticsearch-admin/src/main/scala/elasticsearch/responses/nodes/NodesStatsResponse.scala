/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.nodes

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-stats.html
 *
 * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param metric Limit the information returned to the specified metrics
 * @param indexMetric Limit the information returned for `indices` metric to the specific index metrics. Isn't used if `indices` (or `all`) metric isn't specified.
 * @param fielddataFields A comma-separated list of fields for `fielddata` index metric (supports wildcards)
 * @param groups A comma-separated list of search groups for `search` index metric
 * @param completionFields A comma-separated list of fields for `fielddata` and `suggest` index metric (supports wildcards)
 * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
 * @param fields A comma-separated list of fields for `fielddata` and `completion` index metric (supports wildcards)
 * @param types A comma-separated list of document types for the `indexing` index metric
 * @param timeout Explicit operation timeout
 * @param level Return indices stats aggregated at index, node or shard level
 */
@JsonCodec
final case class NodesStatsResponse() {}
