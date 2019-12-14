/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import elasticsearch.queries.Query
import io.circe.derivation.annotations.JsonCodec
@JsonCodec
final case class AliasDefinition(filter: Option[Query] = None)
