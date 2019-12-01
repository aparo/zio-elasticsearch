/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.cluster

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-update-settings.html
 *
 * @param body body the body of the call
 * @param flatSettings Return settings in flat format (default: false)
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param timeout Explicit operation timeout
 */
@JsonCodec
case class ClusterUpdateSettingsResponse() {}
