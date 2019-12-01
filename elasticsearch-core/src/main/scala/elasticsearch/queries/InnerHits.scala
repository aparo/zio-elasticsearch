/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.queries

import elasticsearch.sort.Sort.Sort
import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey

@JsonCodec
case class InnerHits(
  name: String,
  @JsonKey("ignore_unmapped") ignoreUnmapped: Option[Boolean] = None,
  version: Boolean = false,
  explain: Boolean = false,
  @JsonKey("track_scores") trackScores: Boolean = false,
  from: Int = 0,
  size: Int = 10,
  sort: Sort
)
