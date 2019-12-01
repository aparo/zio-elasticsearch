/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._
/*
 * Updates an alias to point to a new index when the existing index
is considered to be too large or too old.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-rollover-index.html
 *
 * @param alias The name of the alias to rollover
 * @param body body the body of the call
 * @param dryRun If set to true the rollover action will only be validated but not actually performed even if a condition matches. The default is false
 * @param includeTypeName Whether a type should be included in the body of the mappings.
 * @param masterTimeout Specify timeout for connection to master
 * @param newIndex The name of the rollover index
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Set the number of active shards to wait for on the newly created rollover index before the operation returns.
 */
@JsonCodec
final case class IndicesRolloverResponse() {}
