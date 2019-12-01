/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._
/*
 * Returns whether the cluster is running.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html
 *

 */
@JsonCodec
final case class PingResponse() {}
