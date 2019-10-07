/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-recovery.html
 *
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param detailed Whether to display detailed information about shard recovery
 * @param activeOnly Display only those recoveries that are currently on-going
 */
@JsonCodec
case class RecoveryResponse() {}
