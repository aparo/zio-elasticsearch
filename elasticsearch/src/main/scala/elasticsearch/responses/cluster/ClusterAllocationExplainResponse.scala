/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.cluster

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-allocation-explain.html
 *
 * @param body body the body of the call
 * @param includeYesDecisions Return 'YES' decisions in explanation (default: false)
 * @param includeDiskInfo Return information about disk usage and shard sizes (default: false)
 */
@JsonCodec
case class ClusterAllocationExplainResponse() {}
