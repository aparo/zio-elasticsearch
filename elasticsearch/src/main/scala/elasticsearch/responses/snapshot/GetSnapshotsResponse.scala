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
 * @param repository A repository name
 * @param snapshot A comma-separated list of snapshot names
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param ignoreUnavailable Whether to ignore unavailable snapshots, defaults to false which means a SnapshotMissingException is thrown
 */
@JsonCodec
final case class GetSnapshotsResponse() {}
