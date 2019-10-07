/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.snapshot

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
 *
 * @param repository A comma-separated list of repository names
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param local Return local information, do not retrieve the state from master node (default: false)
 */
@JsonCodec
final case class GetRepositoriesResponse() {}
