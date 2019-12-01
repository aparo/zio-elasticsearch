/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.exception
import io.circe.derivation.annotations.JsonCodec

/*
  Logging payload for any any thing not handled by the Http request error logging.
 */
@JsonCodec
case class ErrorLog(
  errorType: ErrorType,
  errorCode: String,
  errorMessage: String,
  thread: Option[String],
  payload: Map[String, String] = Map.empty[String, String],
  details: Map[String, List[ValidationEnvelope]] = Map.empty
)
