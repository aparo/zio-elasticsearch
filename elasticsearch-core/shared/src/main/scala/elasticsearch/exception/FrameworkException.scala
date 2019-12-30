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

package elasticsearch.exception

import scala.language.implicitConversions
import cats.data.NonEmptyList
import io.circe._
import io.circe.derivation.annotations.{Configuration, JsonCodec}
import io.circe.syntax._
import elasticsearch.responses.ErrorResponse
import io.circe.Decoder.Result
import zio.common.ThrowableUtils
import zio.exception.DataException.register
import zio.exception._

trait ElasticSearchException extends FrameworkException

/* ElasticSearch */
/****************************************
 *  Elasticsearch Exceptions
 ****************************************/
@JsonCodec(Configuration.default.withDiscriminator("type"))
sealed trait ElasticSearchSearchException extends FrameworkException {
  override def toJsonObject: JsonObject =
    implicitly[Encoder.AsObject[ElasticSearchSearchException]]
      .encodeObject(this)
      .add(FrameworkException.FAMILY, Json.fromString("ElasticSearchSearchException"))
}



object ElasticSearchSearchException extends ExceptionFamily {
  register("ElasticSearchSearchException", this)
  override def decode(c: HCursor): Result[FrameworkException] = implicitly[Decoder[ElasticSearchSearchException]].apply(c)

  implicit def convertDecodeError(
                                   error: DecodingFailure
                                 ): ElasticSearchParsingException =
    new ElasticSearchParsingException(error.message)

  def apply(msg: String, status: Int, json: Json) =
    new SearchPhaseExecutionException(msg, status, json = json)

  /*
   * Build an error
   */
  def buildException(data: Json, status: Int = ErrorCode.InternalServerError): FrameworkException =
    data match {
      case Json.Null =>
        if (status == 404) new NotFound() else new NotFound() //TODO improve
      case d: Json if d.isObject =>
        d.as[ErrorResponse] match {
          case Left(ex) =>
            ElasticSearchQueryException(
              d.noSpaces,
              status = 500,
              json = data
            )

          case Right(errorResponse) =>
            val errorType = errorResponse.error.`type`
            errorType match {
              case _ =>
                ElasticSearchQueryException(
                  errorResponse.error.reason,
                  status = 500,
                  json = data
                )
            }
        }
      case default =>
        InvalidValueException(s"Not valid value $default")

    }
  //  private def removeErrorType(errorType: String, str: String): String =
  //    str.substring(errorType.size + 1, str.size - 2)
}

@JsonCodec
final case class MultiDocumentException(
  message: String,
  json: Json = Json.Null,
  status: Int = ErrorCode.InternalServerError,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "record.multiple"
) extends ElasticSearchSearchException

@JsonCodec
final case class ValidationError(
  field: String,
  message: String,
  status: Int = ErrorCode.InternalServerError,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "validation.error"
) extends Throwable(message)

@JsonCodec
final case class ElasticSearchIllegalStateException(
  message: String,
  errorType: ErrorType = ErrorType.UnknownError,
  errorCode: String = "elasticsearch.error",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class ElasticSearchParsingException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.parsing",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class ElasticSearchDeleteException(
  message: String,
  status: Int = ErrorCode.InternalServerError,
  errorType: ErrorType = ErrorType.UnknownError,
  errorCode: String = "elasticsearch.delete",
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class ElasticSearchScriptException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.script",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class ElasticSearchQueryException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.query",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null
) extends ElasticSearchSearchException


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
) extends ElasticSearchSearchException

@JsonCodec
final case class NotFound(
  message: String = "Not Found",
  errorType: ErrorType = ErrorType.ProcessingError,
  errorCode: String = "elasticsearch.notfound",
  status: Int = 404,
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class InvalidQuery(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class InvalidParameterQuery(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class QueryError(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class QueryParameterError(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class ScriptFieldsError(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  json: Json = Json.Null
) extends ElasticSearchSearchException

/**
 * This class defines a InvalidParameter entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 * @param json a Json entity
 */
@JsonCodec
final case class InvalidParameter(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.invalidparameter",
  status: Int = ErrorCode.BadRequest,
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class MergeMappingException(
  message: String,
  solution: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.mapping",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null
) extends ElasticSearchSearchException

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
) extends ElasticSearchSearchException

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
) extends ElasticSearchSearchException

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
) extends ElasticSearchSearchException

@JsonCodec
final case class ElasticSearchSearchIllegalArgumentException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invaliddata",
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class IndexNotFoundException(
  message: String,
  status: Int = 404,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.missing",
  json: Json = Json.Null
) extends ElasticSearchSearchException

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
) extends ElasticSearchSearchException

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
) extends ElasticSearchSearchException

/**
 * This class defines a VersionConflictEngineException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 * @param json a Json entity
 */
@JsonCodec
final case class IndexAlreadyExistsException(
  message: String,
  status: Int = 409,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.exists",
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class SearchPhaseExecutionException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.search",
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class ReplicationShardOperationFailedException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.search",
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class ClusterBlockException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.cluster",
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class MapperParsingException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.mapping",
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class ReduceSearchPhaseException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.search",
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class TypeMissingException(
  message: String,
  status: Int = 404,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.type",
  json: Json = Json.Null
) extends ElasticSearchSearchException

//mappings

@JsonCodec
final case class MappedFieldNotFoundException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.mapping",
  json: Json = Json.Null
) extends ElasticSearchSearchException

//storage

@JsonCodec
final case class DataStorageUndefinedException(
  message: String,
  stacktrace: Option[String] = None,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "datastore.missing",
  status: Int = ErrorCode.InternalServerError
) extends ElasticSearchSearchException

@JsonCodec
final case class InvalidStorageTypeException(
  error: String,
  message: String,
  stacktrace: Option[String] = None,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "datastore.missing",
  status: Int = ErrorCode.InternalServerError
) extends ElasticSearchSearchException

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
) extends ElasticSearchSearchException

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
) extends ElasticSearchSearchException

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
) extends ElasticSearchSearchException

/**
 * This class defines a WriteException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class WriteException(
  message: String,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "datastore.write",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends ElasticSearchSearchException

/**
 * This class defines a PropertyNotFoundException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class PropertyNotFoundException(
  message: String,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "config.error",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends ElasticSearchSearchException

/**
 * This class defines a FrameworkMultipleExceptions entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 * @param exceptions a list of FrameworkException entities
 */
@JsonCodec
final case class FrameworkMultipleExceptions(
  message: String,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "framework.exceptions",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError,
  exceptions: Seq[GenericFrameworkException] = Nil
) extends ElasticSearchSearchException

object FrameworkMultipleExceptions {

  def apply(exceptions: Seq[FrameworkException]): FrameworkMultipleExceptions =
    new FrameworkMultipleExceptions(exceptions.map(_.message).mkString("\n"), exceptions = exceptions.map(_.toGeneric))
}

/**
 * Exceptions used in parsing values
 */
/**
 * Exceptions used in parsing values
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class NoTypeParser(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.missing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends ElasticSearchSearchException

object NoTypeParser {
  lazy val default = NoTypeParser("Not type parser defined!")
}

/**
 * This class defines a ScriptingEngineNotFound entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class ScriptingEngineNotFound(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "scripting.missing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends ElasticSearchSearchException

/**
 * This class defines a MissingScriptException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class MissingScriptException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "scripting.missing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends ElasticSearchSearchException

/**
 * This class defines a ValidationException entity
 * @param validationErrors a NonEmptyList[ValidationError] entity
 * @param message the error message
 * @param modelName a String
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 */
@JsonCodec
case class ValidationException(
  validationErrors: NonEmptyList[ValidationError],
  message: String = "validation.error",
  modelName: Option[String] = None,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "validation.error",
  status: Int = ErrorCode.BadRequest
) extends ElasticSearchSearchException {}

object ValidationException {

  def requiredFieldException(field: String, message: String): ValidationException =
    ValidationException(validationErrors = NonEmptyList.of(ValidationError(field, message)))
}

/****************************************
 *  AUTH Exceptions
  ****************************************/
@JsonCodec
sealed trait AuthException extends FrameworkException

/**
 * This class defines a UserNotFoundException entity
 * @param userId an User Id
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 */
@JsonCodec
case class UserNotFoundException(
  userId: String,
  message: String = "auth.error",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.NotFound
) extends AuthException

/**
 * This exception is throw if the permission string is malformed
 * @param permissionString the permission that is not valid
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 */
@JsonCodec
case class InvalidPermissionStringException(
  permissionString: String,
  message: String = "auth.error",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.NotFound
) extends AuthException

/**
 * This exception is thrown when a property is missing
 * @param userId an User Id
 * @param property a property to look for
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 */
@JsonCodec
case class UserPropertyNotFoundException(
  userId: String,
  property: String,
  message: String = "auth.error",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.NotFound
) extends AuthException
