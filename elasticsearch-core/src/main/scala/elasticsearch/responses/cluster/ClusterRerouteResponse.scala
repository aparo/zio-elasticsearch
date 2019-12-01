/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.cluster

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-reroute.html
 *
 * @param body body the body of the call
 * @param explain Return an explanation of why the commands can or cannot be executed
 * @param dryRun Simulate the operation only and return the resulting state
 * @param metric Limit the information returned to the specified metrics. Defaults to all but metadata
 * @param retryFailed Retries allocation of shards that are blocked due to too many subsequent allocation failures
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class ClusterRerouteResponse() {}
