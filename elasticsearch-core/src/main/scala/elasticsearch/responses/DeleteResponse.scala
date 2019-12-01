/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-delete.html
 *
 */
@JsonCodec
final case class DeleteResponse(
    @JsonKey("_index") index: String,
    @JsonKey("_id") id: String,
    @JsonKey("_shards") shards: Shards = Shards.empty,
    @JsonKey("_version") version: Long = 0,
    result: Option[String] = None,
    found: Boolean = false
)
