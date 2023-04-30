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

import zio.json._
import zio.json.ast.Json

/**
 * ************************************** Data Exceptions
 */
@jsonDiscriminator("type")
sealed trait DataException extends FrameworkException {
  override def toJsonWithFamily: Either[String, Json] = for {
    json <- implicitly[JsonEncoder[DataException]].toJsonAST(this)
    jsonFamily <- addFamily(json, "DataException")
  } yield jsonFamily

}

object DataException extends ExceptionFamily {
  register("DataException", this)
  implicit final val jsonDecoder: JsonDecoder[DataException] =
    DeriveJsonDecoder.gen[DataException]
  implicit final val jsonEncoder: JsonEncoder[DataException] =
    DeriveJsonEncoder.gen[DataException]
  implicit final val jsonCodec: JsonCodec[DataException] = JsonCodec(jsonEncoder, jsonDecoder)
  override def decode(c: Json): Either[String, FrameworkException] =
    implicitly[JsonDecoder[DataException]].fromJsonAST(c)
}

/**
 * This class defines a MissingRecordException entity
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
final case class MissingRecordException(
  error: String,
  message: String = "Missing Record",
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "record.missing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends DataException
object MissingRecordException {
  implicit val jsonDecoder: JsonDecoder[MissingRecordException] = DeriveJsonDecoder.gen[MissingRecordException]
  implicit val jsonEncoder: JsonEncoder[MissingRecordException] = DeriveJsonEncoder.gen[MissingRecordException]
}

/**
 * This class defines a RecordProcessingException entity
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
final case class RecordProcessingException(
  error: String,
  message: String = "Processing Record Exception",
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "record.processing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends DataException
object RecordProcessingException {
  implicit val jsonDecoder: JsonDecoder[RecordProcessingException] = DeriveJsonDecoder.gen[RecordProcessingException]
  implicit val jsonEncoder: JsonEncoder[RecordProcessingException] = DeriveJsonEncoder.gen[RecordProcessingException]
}

/**
 * This class defines a NoServerAvailableException entity
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 * @param stacktrace
 *   the stacktrace of the exception
 * @param json
 *   a Json entity
 */
@jsonMemberNames(SnakeCase)
final case class NoServerAvailableException(
  message: String,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "framework.noserver",
  status: Int = ErrorCode.InternalServerError,
  stacktrace: Option[String] = None,
  json: Json = Json.Null
) extends DataException
object NoServerAvailableException {
  implicit val jsonDecoder: JsonDecoder[NoServerAvailableException] = DeriveJsonDecoder.gen[NoServerAvailableException]
  implicit val jsonEncoder: JsonEncoder[NoServerAvailableException] = DeriveJsonEncoder.gen[NoServerAvailableException]
}

/**
 * This class defines a InvalidValueException entity
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 * @param json
 *   a Json entity
 */
@jsonMemberNames(SnakeCase)
final case class InvalidValueException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.invalidparameter",
  status: Int = ErrorCode.BadRequest,
  stacktrace: Option[String] = None,
  json: Json = Json.Null
) extends DataException
object InvalidValueException {
  implicit val jsonDecoder: JsonDecoder[InvalidValueException] = DeriveJsonDecoder.gen[InvalidValueException]
  implicit val jsonEncoder: JsonEncoder[InvalidValueException] = DeriveJsonEncoder.gen[InvalidValueException]
}

/**
 * This class defines a MissingValueException entity
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 * @param json
 *   a Json entity
 */
@jsonMemberNames(SnakeCase)
final case class MissingValueException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.missing",
  status: Int = ErrorCode.NotFound,
  stacktrace: Option[String] = None,
  json: Json = Json.Null
) extends DataException
object MissingValueException {
  implicit val jsonDecoder: JsonDecoder[MissingValueException] = DeriveJsonDecoder.gen[MissingValueException]
  implicit val jsonEncoder: JsonEncoder[MissingValueException] = DeriveJsonEncoder.gen[MissingValueException]
}

/**
 * This class defines a MissingFieldException entity
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 * @param json
 *   a Json entity
 */
@jsonMemberNames(SnakeCase)
final case class MissingFieldException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.missing",
  status: Int = ErrorCode.NotFound,
  stacktrace: Option[String] = None,
  json: Json = Json.Null
) extends DataException
object MissingFieldException {
  implicit val jsonDecoder: JsonDecoder[MissingFieldException] = DeriveJsonDecoder.gen[MissingFieldException]
  implicit val jsonEncoder: JsonEncoder[MissingFieldException] = DeriveJsonEncoder.gen[MissingFieldException]
}

/**
 * This class defines a NotUniqueValueException entity
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 * @param json
 *   a Json entity
 */
@jsonMemberNames(SnakeCase)
final case class NotUniqueValueException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.notunique",
  status: Int = ErrorCode.BadRequest,
  stacktrace: Option[String] = None,
  json: Json = Json.Null
)
object NotUniqueValueException {
  implicit val jsonDecoder: JsonDecoder[NotUniqueValueException] = DeriveJsonDecoder.gen[NotUniqueValueException]
  implicit val jsonEncoder: JsonEncoder[NotUniqueValueException] = DeriveJsonEncoder.gen[NotUniqueValueException]
}

/**
 * This class defines a NotFoundException entity
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 * @param json
 *   a Json entity
 */
@jsonMemberNames(SnakeCase)
final case class NotFoundException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.missing",
  status: Int = ErrorCode.NotFound,
  stacktrace: Option[String] = None,
  json: Json = Json.Null
) extends DataException
object NotFoundException {
  implicit val jsonDecoder: JsonDecoder[NotFoundException] = DeriveJsonDecoder.gen[NotFoundException]
  implicit val jsonEncoder: JsonEncoder[NotFoundException] = DeriveJsonEncoder.gen[NotFoundException]
}

/**
 * This class defines a AlreadyExistsException entity
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 * @param json
 *   a Json entity
 */
@jsonMemberNames(SnakeCase)
final case class AlreadyExistsException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.exists",
  status: Int = ErrorCode.Conflict,
  stacktrace: Option[String] = None,
  json: Json = Json.Null
) extends DataException
object AlreadyExistsException {
  implicit val jsonDecoder: JsonDecoder[AlreadyExistsException] = DeriveJsonDecoder.gen[AlreadyExistsException]
  implicit val jsonEncoder: JsonEncoder[AlreadyExistsException] = DeriveJsonEncoder.gen[AlreadyExistsException]
}

/**
 * This class defines a VersionConflictEngineException entity
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 * @param json
 *   a Json entity
 */
@jsonMemberNames(SnakeCase)
final case class VersionConflictEngineException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.exists",
  status: Int = ErrorCode.Conflict,
  stacktrace: Option[String] = None,
  json: Json = Json.Null
) extends DataException
object VersionConflictEngineException {
  implicit val jsonDecoder: JsonDecoder[VersionConflictEngineException] =
    DeriveJsonDecoder.gen[VersionConflictEngineException]
  implicit val jsonEncoder: JsonEncoder[VersionConflictEngineException] =
    DeriveJsonEncoder.gen[VersionConflictEngineException]
}

/**
 * This class defines a DocumentAlreadyExistsException entity
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 * @param json
 *   a Json entity
 */
final case class DocumentAlreadyExistsException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.exists",
  status: Int = ErrorCode.Conflict,
  stacktrace: Option[String] = None,
  json: Json = Json.Null
) extends DataException
@jsonMemberNames(SnakeCase)
object DocumentAlreadyExistsException {
  implicit val jsonDecoder: JsonDecoder[DocumentAlreadyExistsException] =
    DeriveJsonDecoder.gen[DocumentAlreadyExistsException]
  implicit val jsonEncoder: JsonEncoder[DocumentAlreadyExistsException] =
    DeriveJsonEncoder.gen[DocumentAlreadyExistsException]
}

/**
 * This class defines a DocumentAlreadyExistsEngineException entity
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 * @param json
 *   a Json entity
 */
@jsonMemberNames(SnakeCase)
final case class DocumentAlreadyExistsEngineException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.exists",
  status: Int = ErrorCode.Conflict,
  stacktrace: Option[String] = None,
  json: Json = Json.Null
) extends DataException
object DocumentAlreadyExistsEngineException {
  implicit val jsonDecoder: JsonDecoder[DocumentAlreadyExistsEngineException] =
    DeriveJsonDecoder.gen[DocumentAlreadyExistsEngineException]
  implicit val jsonEncoder: JsonEncoder[DocumentAlreadyExistsEngineException] =
    DeriveJsonEncoder.gen[DocumentAlreadyExistsEngineException]
}

/**
 * Exceptions used in parsing values
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
final case class NoTypeParserException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.missing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends DataException

object NoTypeParserException {
  lazy val default: NoTypeParserException = NoTypeParserException("Not type parser defined!")
  implicit val jsonDecoder: JsonDecoder[NoTypeParserException] = DeriveJsonDecoder.gen[NoTypeParserException]
  implicit val jsonEncoder: JsonEncoder[NoTypeParserException] = DeriveJsonEncoder.gen[NoTypeParserException]
}

/**
 * This class defines a InvalidJsonException entity
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 * @param json
 *   a Json entity
 */
@jsonMemberNames(SnakeCase)
final case class InvalidJsonException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.exists",
  status: Int = ErrorCode.Conflict,
  stacktrace: Option[String] = None,
  json: Json = Json.Null
) extends DataException
object InvalidJsonException {
  implicit val jsonCodec: JsonCodec[InvalidJsonException] =
    DeriveJsonCodec.gen[InvalidJsonException]
}
