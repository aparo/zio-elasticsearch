/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.exception
import io.circe.derivation.annotations.JsonCodec

/*
  Logging payload on any Http request error.
 */
@JsonCodec
case class HttpErrorLog(
  method: String,
  uri: String,
  errorType: ErrorType,
  errorCode: String,
  errorMessage: String,
  thread: Option[String],
  payload: Map[String, String] = Map.empty[String, String],
  validationPayload: Map[String, List[ValidationEnvelope]] = Map.empty[String, List[ValidationEnvelope]]
)

@JsonCodec
case class ValidationEnvelope(key: String, message: String)
