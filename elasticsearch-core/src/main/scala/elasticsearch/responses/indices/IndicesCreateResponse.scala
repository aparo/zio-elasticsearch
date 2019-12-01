/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._
/*
 * Creates an index with optional settings and mappings.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-create-index.html
 *
 * @param index The name of the index
 * @param body body the body of the call
 * @param includeTypeName Whether a type should be expected in the body of the mappings.
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Set the number of active shards to wait for before the operation returns.
 */
@JsonCodec
final case class IndicesCreateResponse(acknowledged: Boolean) {}
