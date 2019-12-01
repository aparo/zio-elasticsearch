/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.cat

import io.circe.derivation.annotations._
/*
 * Returns basic statistics about performance of cluster nodes.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-nodes.html
 *
 * @param bytes The unit in which to display byte values
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param fullId Return the full node ID instead of the shortened version (default: false)
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param time The unit in which to display time values
 * @param v Verbose mode. Display column headers
 */
@JsonCodec
final case class CatNodesResponse() {}
