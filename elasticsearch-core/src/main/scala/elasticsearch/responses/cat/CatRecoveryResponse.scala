/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.cat

import io.circe.derivation.annotations._
/*
 * Returns information about index shard recoveries, both on-going completed.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-recovery.html
 *
 * @param activeOnly If `true`, the response only includes ongoing shard recoveries
 * @param bytes The unit in which to display byte values
 * @param detailed If `true`, the response includes detailed information about shard recoveries
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param index Comma-separated list or wildcard expression of index names to limit the returned information
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param time The unit in which to display time values
 * @param v Verbose mode. Display column headers
 */
@JsonCodec
final case class CatRecoveryResponse() {}
