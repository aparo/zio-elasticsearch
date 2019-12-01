/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._

@JsonCodec
final case class ErrorRoot(
    @JsonKey("type") `type`: String,
    reason: String,
    @JsonKey("resource.type") resourceType: Option[String] = None,
    @JsonKey("resource.id") resourceId: Option[String] = None,
    @JsonKey("index_uuid") indexUUID: Option[String] = None,
    index: Option[String] = None,
    shard: Option[String] = None
)

@JsonCodec
final case class Error(
    @JsonKey("type") `type`: String,
    reason: String,
    @JsonKey("root_cause") rootCause: List[ErrorRoot] = Nil
)

@JsonCodec
final case class ErrorResponse(error: Error, status: Int = 500)

//Test
