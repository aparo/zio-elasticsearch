/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.schema.elasticsearch

import zio.exception._
import zio.json._
import zio.json.ast._

@jsonDiscriminator("type")
sealed trait SchemaException extends FrameworkException {
  override def toJsonWithFamily: Either[String, Json] = for {
    json <- implicitly[JsonEncoder[SchemaException]].toJsonAST(this)
    jsonFamily <- addFamily(json, "SchemaException")
  } yield jsonFamily
}

object SchemaException extends ExceptionFamily {
  register("SchemaException", this)
  implicit final val jsonDecoder: JsonDecoder[SchemaException] =
    DeriveJsonDecoder.gen[SchemaException]
  implicit final val jsonEncoder: JsonEncoder[SchemaException] =
    DeriveJsonEncoder.gen[SchemaException]
  implicit final val jsonCodec: JsonCodec[SchemaException] = JsonCodec(jsonEncoder, jsonDecoder)
  override def decode(c: Json): Either[String, FrameworkException] =
    implicitly[JsonDecoder[SchemaException]].fromJsonAST(c)
}

/**
 * This class defines a SchemaNotFoundException entity
 *
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
final case class SchemaNotFoundException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "schema.missing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends SchemaException
object SchemaNotFoundException {
  implicit val jsonDecoder: JsonDecoder[SchemaNotFoundException] = DeriveJsonDecoder.gen[SchemaNotFoundException]
  implicit val jsonEncoder: JsonEncoder[SchemaNotFoundException] = DeriveJsonEncoder.gen[SchemaNotFoundException]
}

/**
 * This class defines a SchemaManagerException entity
 *
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
final case class SchemaManagerException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "schema.exception",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends SchemaException
object SchemaManagerException {
  implicit val jsonDecoder: JsonDecoder[SchemaManagerException] = DeriveJsonDecoder.gen[SchemaManagerException]
  implicit val jsonEncoder: JsonEncoder[SchemaManagerException] = DeriveJsonEncoder.gen[SchemaManagerException]
}

/**
 * This class defines a ScriptingEngineNotFound entity
 *
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
final case class UnableToRegisterSchemaException(
  message: String,
  errorType: ErrorType = ErrorType.SchemaError,
  errorCode: String = "schema.invalid",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends SchemaException
object UnableToRegisterSchemaException {
  implicit val jsonDecoder: JsonDecoder[UnableToRegisterSchemaException] =
    DeriveJsonDecoder.gen[UnableToRegisterSchemaException]
  implicit val jsonEncoder: JsonEncoder[UnableToRegisterSchemaException] =
    DeriveJsonEncoder.gen[UnableToRegisterSchemaException]
}
