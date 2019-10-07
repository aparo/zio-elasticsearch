/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-create-index.html
 *
 * @param index The name of the index
 * @param body body the body of the call
 * @param waitForActiveShards Set the number of active shards to wait for before the operation returns.
 * @param timeout Explicit operation timeout
 * @param masterTimeout Specify timeout for connection to master
 * @param updateAllTypes Whether to update the mapping for all fields with the same name across all types or not
 */
@JsonCodec
case class CreateIndexResponse(acknowledged: Boolean)
