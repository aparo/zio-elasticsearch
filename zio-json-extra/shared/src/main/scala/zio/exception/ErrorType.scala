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

import zio.json.extra._
import zio.json._

sealed trait ErrorType

case object ErrorType {

  case object AccessDeniedError extends ErrorType

  case object AuthError extends ErrorType

  case object ConfigurationError extends ErrorType

  case object DBError extends ErrorType

  case object MetricError extends ErrorType

  case object ProcessingError extends ErrorType

  case object ServerError extends ErrorType

  case object ServiceError extends ErrorType

  case object ValidationError extends ErrorType

  case object SchemaError extends ErrorType

  case object UnknownError extends ErrorType

  case object StoreError extends ErrorType

  implicit final val jsonDecoder: JsonDecoder[ErrorType] =
    DeriveJsonDecoderEnum.gen[ErrorType]
  implicit final val jsonEncoder: JsonEncoder[ErrorType] =
    DeriveJsonEncoderEnum.gen[ErrorType]
  implicit final val jsonCodec: JsonCodec[ErrorType] = JsonCodec(jsonEncoder, jsonDecoder)

}
