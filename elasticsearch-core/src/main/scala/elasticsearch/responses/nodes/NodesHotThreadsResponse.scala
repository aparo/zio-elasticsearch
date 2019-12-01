/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.nodes

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-hot-threads.html
 *
 * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param snapshots Number of samples of thread stacktrace (default: 10)
 * @param interval The interval for the second sampling of threads
 * @param `type` The type to sample (default: cpu)
 * @param ignoreIdleThreads Don't show threads that are in known-idle places, such as waiting on a socket select or pulling from an empty task queue (default: true)
 * @param threads Specify the number of threads to provide information for (default: 3)
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class NodesHotThreadsResponse() {}
