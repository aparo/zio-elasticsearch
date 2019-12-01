/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations.JsonCodec
@JsonCodec
case class IndicesExistsResponse(exists: Boolean = false) {
  def isExists: Boolean = exists
}
