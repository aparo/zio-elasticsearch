/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.aggregations

import io.circe.derivation.annotations.JsonCodec
import io.circe._

@JsonCodec
final case class RangeValue(
  key: Option[String] = None,
  from: Option[Json] = None,
  to: Option[Json] = None
)
