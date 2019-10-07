/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
 *
 */
@JsonCodec
final case class ExistsResponse(
  @JsonKey("_index") index: String,
  @JsonKey("_type") docType: String,
  @JsonKey("_id") id: String,
  @JsonKey("_version") version: Long,
  result: Option[String] = None,
  found: Boolean = false
) {
  def exists(): Boolean = found
}
