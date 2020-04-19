/*
 * Copyright 2019-2020 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zio.exception
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
