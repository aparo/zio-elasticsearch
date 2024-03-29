/*
 * Copyright 2019 Alberto Paro
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
import zio.json._

/*
  Response payload on any error
 */
final case class ErrorEnvelope(
  errorType: ErrorType,
  correlationId: String,
  errorCode: String,
  errorMessage: String,
  details: Map[String, List[ValidationEnvelope]] = Map.empty
)
object ErrorEnvelope {
  implicit val jsonDecoder: JsonDecoder[ErrorEnvelope] = DeriveJsonDecoder.gen[ErrorEnvelope]
  implicit val jsonEncoder: JsonEncoder[ErrorEnvelope] = DeriveJsonEncoder.gen[ErrorEnvelope]
}
