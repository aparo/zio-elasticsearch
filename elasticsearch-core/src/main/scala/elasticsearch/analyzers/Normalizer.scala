/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.analyzers
import io.circe.derivation.annotations.JsonCodec

@JsonCodec
final case class Normalizer(`type`: String,
                            filter: List[String] = Nil,
                            char_filter: List[String] = Nil)
