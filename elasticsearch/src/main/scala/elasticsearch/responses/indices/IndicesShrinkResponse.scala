/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._
/*
 * Allow to shrink an existing index into a new index with fewer primary shards.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-shrink-index.html
 *
 * @param index The name of the source index to shrink
 * @param target The name of the target index to shrink into
 * @param body body the body of the call
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Set the number of active shards to wait for on the shrunken index before the operation returns.
 */
@JsonCodec
final case class IndicesShrinkResponse() {}
