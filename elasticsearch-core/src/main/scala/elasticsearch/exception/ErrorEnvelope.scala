/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.exception
import io.circe.derivation.annotations.JsonCodec

/*
  Response payload on any error
 */
@JsonCodec
case class ErrorEnvelope(
  errorType: ErrorType,
  correlationId: String,
  errorCode: String,
  errorMessage: String,
  details: Map[String, List[ValidationEnvelope]] = Map.empty
)
