/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-rollover-index.html
 *
 * @param alias The name of the alias to rollover
 * @param newIndex The name of the rollover index
 * @param body body the body of the call
 * @param timeout Explicit operation timeout
 * @param dryRun If set to true the rollover action will only be validated but not actually performed even if a condition matches. The default is false
 * @param masterTimeout Specify timeout for connection to master
 * @param waitForActiveShards Set the number of active shards to wait for on the newly created rollover index before the operation returns.
 */
@JsonCodec
case class RolloverResponse() {}
