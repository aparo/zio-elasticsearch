/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.queries

import io.circe.Json
import io.circe.derivation.annotations.JsonCodec

@JsonCodec
final case class Range(from: Option[Json] = None, to: Option[Json] = None)

@JsonCodec
final case class RangeString(
    from: Option[String] = None,
    to: Option[String] = None
)
