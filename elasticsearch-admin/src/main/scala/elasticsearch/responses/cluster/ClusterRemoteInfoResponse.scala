/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.cluster

import io.circe.derivation.annotations._
/*
 * Returns the information about configured remote clusters.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-remote-info.html
 *

 */
@JsonCodec
final case class ClusterRemoteInfoResponse() {}
