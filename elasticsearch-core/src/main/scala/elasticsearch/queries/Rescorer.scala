/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.queries

import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey

@JsonCodec
final case class Rescorer(
    query: Query,
    @JsonKey("rescore_query_weight") rescoreQueryWeight: Option[Float] = None,
    @JsonKey("query_weight") queryWeight: Option[Float] = None,
    @JsonKey("score_mode") scoreMode: Option[String] = None
)
