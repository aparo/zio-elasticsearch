/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._
/*
 * Changes the number of requests per second for a particular Update By Query operation.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update-by-query.html
 *
 * @param requestsPerSecond The throttle to set on this request in floating sub-requests per second. -1 means set no throttle.
 * @param taskId The task id to rethrottle
 */
@JsonCodec
final case class UpdateByQueryRethrottleResponse() {}
