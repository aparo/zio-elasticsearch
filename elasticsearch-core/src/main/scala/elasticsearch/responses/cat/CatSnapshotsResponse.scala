/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.cat

import io.circe.derivation.annotations._
/*
 * Returns all snapshots in a specific repository.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-snapshots.html
 *
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param ignoreUnavailable Set to true to ignore unavailable snapshots
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param repository Name of repository from which to fetch the snapshot information
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param time The unit in which to display time values
 * @param v Verbose mode. Display column headers
 */
@JsonCodec
final case class CatSnapshotsResponse() {}
