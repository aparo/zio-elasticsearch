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

package zio.exception

import zio.json.ast._
import zio.json._
import zio.schema.elasticsearch.SchemaField

/**
 * Schema Exceptions
 */

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
 * This class defines a MergeSchemaException entity
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
@jsonMemberNames(SnakeCase)
final case class MergeSchemaException(
  error: String,
  schemaFields: List[SchemaField],
  message: String = "Unable to merge schemas",
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "schema.merging",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends SchemaException
object MergeSchemaException {
  implicit val jsonDecoder: JsonDecoder[MergeSchemaException] = DeriveJsonDecoder.gen[MergeSchemaException]
  implicit val jsonEncoder: JsonEncoder[MergeSchemaException] = DeriveJsonEncoder.gen[MergeSchemaException]
}

/**
 * Exceptions used in schema validation
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
@jsonMemberNames(SnakeCase)
final case class SchemaValidationException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "schema.validation",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends SchemaException

object SchemaValidationException {
  lazy val invalidFormat: SchemaValidationException = SchemaValidationException("Invalid Format")
  implicit val jsonDecoder: JsonDecoder[SchemaValidationException] = DeriveJsonDecoder.gen[SchemaValidationException]
  implicit val jsonEncoder: JsonEncoder[SchemaValidationException] = DeriveJsonEncoder.gen[SchemaValidationException]
}
