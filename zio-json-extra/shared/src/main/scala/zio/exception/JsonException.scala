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

import zio.json.ast.Json
import zio.json._

/**
 * ************************************** AUTH Exceptions
 */
@jsonDiscriminator("type")
sealed trait JsonException extends FrameworkException {

  override def toJsonWithFamily: Either[String, Json] = for {
    json <- implicitly[JsonEncoder[JsonException]].toJsonAST(this)
    jsonFamily <- addFamily(json, "JsonException")
  } yield jsonFamily
}

object JsonException extends ExceptionFamily {
  register("JsonException", this)
  implicit final val jsonDecoder: JsonDecoder[JsonException] =
    DeriveJsonDecoder.gen[JsonException]
  implicit final val jsonEncoder: JsonEncoder[JsonException] =
    DeriveJsonEncoder.gen[JsonException]
  implicit final val jsonCodec: JsonCodec[JsonException] = JsonCodec(jsonEncoder, jsonDecoder)
  override def decode(c: Json): Either[String, FrameworkException] =
    implicitly[JsonDecoder[JsonException]].fromJsonAST(c)
}

/**
 * This class defines a FrameworkDecodingFailure entity
 * @param error
 *   a string representing the error
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param stacktrace
 *   the stacktrace of the exception
 * @param status
 *   HTTP Error Status
 */
final case class DecodingFailureException(
  error: String,
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.decoding",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends JsonException
object DecodingFailureException {
  implicit final val jsonDecoder: JsonDecoder[DecodingFailureException] =
    DeriveJsonDecoder.gen[DecodingFailureException]
  implicit final val jsonEncoder: JsonEncoder[DecodingFailureException] =
    DeriveJsonEncoder.gen[DecodingFailureException]
  implicit final val jsonCodec: JsonCodec[DecodingFailureException] = JsonCodec(jsonEncoder, jsonDecoder)
}

/**
 * This class defines a ConfigurationSourceException entity
 * @param error
 *   a string representing the error
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param stacktrace
 *   the stacktrace of the exception
 * @param status
 *   HTTP Error Status
 */
final case class ParsingFailureException(
  error: String,
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.jsonparsing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends JsonException
object ParsingFailureException {
  implicit final val jsonDecoder: JsonDecoder[ParsingFailureException] =
    DeriveJsonDecoder.gen[ParsingFailureException]
  implicit final val jsonEncoder: JsonEncoder[ParsingFailureException] =
    DeriveJsonEncoder.gen[ParsingFailureException]
  implicit final val jsonCodec: JsonCodec[ParsingFailureException] = JsonCodec(jsonEncoder, jsonDecoder)
}
