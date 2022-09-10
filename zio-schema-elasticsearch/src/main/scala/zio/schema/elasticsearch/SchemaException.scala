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

package zio.schema.elasticsearch

import io.circe.Decoder.Result
import io.circe._
import io.circe.derivation.annotations._
import io.circe.syntax._
import zio.exception._

@JsonCodec(Configuration.default.withDiscriminator("type"))
sealed trait SchemaException extends FrameworkException {
  override def toJsonObject: JsonObject =
    implicitly[Encoder.AsObject[SchemaException]]
      .encodeObject(this)
      .add(FrameworkException.FAMILY, Json.fromString("SchemaException"))
}

object SchemaException extends ExceptionFamily {
  register("SchemaException", this)

  override def decode(c: HCursor): Result[FrameworkException] = implicitly[Decoder[SchemaException]].apply(c)
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
@JsonCodec
final case class SchemaNotFoundException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "schema.missing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends SchemaException {
  override def toJsonObject: JsonObject = this.asJsonObject
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
@JsonCodec
final case class SchemaManagerException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "schema.exception",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends SchemaException {
  override def toJsonObject: JsonObject = this.asJsonObject
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
@JsonCodec
final case class UnableToRegisterSchemaException(
  message: String,
  errorType: ErrorType = ErrorType.SchemaError,
  errorCode: String = "schema.invalid",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends SchemaException {
  override def toJsonObject: JsonObject = this.asJsonObject
}
