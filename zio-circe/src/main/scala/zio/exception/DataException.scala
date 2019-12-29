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

import io.circe.Decoder.Result
import io.circe._
import io.circe.derivation.annotations._
import io.circe.syntax._

/****************************************
 *  AUTH Exceptions
  ****************************************/
@JsonCodec(Configuration.default.withDiscriminator("type"))
sealed trait DataException extends FrameworkException {
  override def toJsonObject: JsonObject =
    implicitly[Encoder.AsObject[DataException]]
      .encodeObject(this)
      .add(FrameworkException.FAMILY, Json.fromString("DataException"))
}

object DataException extends ExceptionFamily {
  register("DataException", this)
  override def decode(c: HCursor): Result[FrameworkException] = implicitly[Decoder[DataException]].apply(c)
}

/**
 * This class defines a MissingRecordException entity
 * @param error a string representing the error
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class MissingRecordException(
  error: String,
  message: String = "Missing Record",
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "record.missing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends DataException {
  override def toJsonObject: JsonObject = this.asJsonObject
}

/**
 * This class defines a RecordProcessingException entity
 * @param error a string representing the error
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class RecordProcessingException(
  error: String,
  message: String = "Processing Record Exception",
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "record.processing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends DataException {
  override def toJsonObject: JsonObject = this.asJsonObject
}

/**
 * This class defines a NoServerAvailableException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 * @param stacktrace the stacktrace of the exception
 * @param json a Json entity
 */
@JsonCodec
final case class NoServerAvailableException(
  message: String,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "framework.noserver",
  status: Int = ErrorCode.InternalServerError,
  stacktrace: Option[String] = None,
  json: Json = Json.Null
) extends DataException {
  override def toJsonObject: JsonObject = this.asJsonObject
}

/**
 * This class defines a InvalidValueException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 * @param json a Json entity
 */
@JsonCodec
final case class InvalidValueException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.invalidparameter",
  status: Int = ErrorCode.BadRequest,
  json: Json = Json.Null
) extends DataException {
  override def toJsonObject: JsonObject = this.asJsonObject
}

/**
 * This class defines a MissingValueException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 * @param json a Json entity
 */
@JsonCodec
final case class MissingValueException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.missing",
  status: Int = ErrorCode.NotFound,
  json: Json = Json.Null
) extends DataException {
  override def toJsonObject: JsonObject = this.asJsonObject
}

/**
 * This class defines a NotUniqueValueException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 * @param json a Json entity
 */
@JsonCodec
final case class NotUniqueValueException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.notunique",
  status: Int = ErrorCode.BadRequest,
  json: Json = Json.Null
) extends DataException {
  override def toJsonObject: JsonObject = this.asJsonObject
}

/**
 * This class defines a NotFoundException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 * @param json a Json entity
 */
@JsonCodec
final case class NotFoundException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.missing",
  status: Int = ErrorCode.NotFound,
  json: Json = Json.Null
) extends DataException {
  override def toJsonObject: JsonObject = this.asJsonObject
}

/**
 * This class defines a AlreadyExistsException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 * @param json a Json entity
 */
@JsonCodec
final case class AlreadyExistsException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.exists",
  status: Int = ErrorCode.Conflict,
  json: Json = Json.Null
) extends DataException {
  override def toJsonObject: JsonObject = this.asJsonObject
}

/**
 * This class defines a VersionConflictEngineException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 * @param json a Json entity
 */
@JsonCodec
final case class VersionConflictEngineException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.exists",
  status: Int = ErrorCode.Conflict,
  json: Json = Json.Null
) extends DataException {
  override def toJsonObject: JsonObject = this.asJsonObject
}

/**
 * This class defines a DocumentAlreadyExistsException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 * @param json a Json entity
 */
@JsonCodec
final case class DocumentAlreadyExistsException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.exists",
  status: Int = ErrorCode.Conflict,
  json: Json = Json.Null
) extends DataException {
  override def toJsonObject: JsonObject = this.asJsonObject
}

/**
 * This class defines a DocumentAlreadyExistsEngineException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 * @param json a Json entity
 */
@JsonCodec
final case class DocumentAlreadyExistsEngineException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.exists",
  status: Int = ErrorCode.Conflict,
  json: Json = Json.Null
) extends DataException {
  override def toJsonObject: JsonObject = this.asJsonObject
}
