/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe._
import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
 *
 */
@JsonCodec
case class GetResponse(
    @JsonKey("_index") index: String,
    @JsonKey("_type") docType: String,
    @JsonKey("_id") id: String,
    @JsonKey("_version") version: Long = 1,
    @JsonKey("_shards") shards: Shards = Shards(),
    found: Boolean = false,
    @JsonKey("_source") source: JsonObject = JsonObject.empty,
    @JsonKey("fields") fields: JsonObject = JsonObject.empty,
    error: Option[ErrorResponse] = None
) {
  def getId: String = id

  def getType: String = docType

  def getIndex: String = index

  def getVersion: Long = version

}
