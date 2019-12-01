/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-index_.html
 *
 */
@JsonCodec
case class IndexResponse(
    @JsonKey("_index") index: String,
    @JsonKey("_id") id: String,
    @JsonKey("_type") docType: String = "_doc",
    @JsonKey("_version") version: Long = 0,
    @JsonKey("_shards") shards: Shards = Shards(),
    result: Option[String] = None,
    _seq_no: Long = 0,
    _primary_term: Long = 0
) {

  def toUpdate: UpdateResponse =
    UpdateResponse(
      index = index,
      id = id,
      version = version,
      shards = shards,
      result = result
    )
}
