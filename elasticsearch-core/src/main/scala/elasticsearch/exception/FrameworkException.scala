/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.exception

import scala.language.implicitConversions
import _root_.elasticsearch.common.ThrowableUtils
import cats.data.NonEmptyList
import io.circe._
import io.circe.derivation.annotations.{ Configuration, JsonCodec }
import io.circe.syntax._
import elasticsearch.responses.ErrorResponse

@JsonCodec(Configuration.encodeOnly)
sealed trait FrameworkException extends Throwable {
  def status: Int

  def errorType: ErrorType

  def errorCode: String

  def message: String

  /**
   *
   * messageParameters: values to be substituted in the messages file
   */
  def messageParameters: Seq[AnyRef] = Seq.empty

  /**
   *   logMap: any key value pair that need to be logged as part of the [[HttpErrorLog]] but is not required to be part of the
   *   error response in the [[ErrorEnvelope]]
   */
  def logMap: Map[String, AnyRef] = Map.empty[String, AnyRef]

  /*
   For the most part, exceptions will be logged globally at the outer edges where the logging thread will most likely be the
   dispatcher thread. However, the actual failure might have occurred on a different thread. Hence we capture this information
   as it might be useful in debugging errors.
   */
  val thread: Option[String] = Some(Thread.currentThread().getName)

  def toErrorJson: Json =
    Json.obj("status" -> Json.fromInt(status), "message" -> Json.fromString(message))

  def toGeneric: GenericFrameworkException =
    GenericFrameworkException(error = errorCode, message = message)
}

/**
 * This class defines a SimpleThrowable entity
 * @param message the error message
 * @param type the type of the SimpleThrowable entity
 * @param stacktrace the stacktrace of the exception
 * @param cause the cause of the exception
 */
@JsonCodec
final case class SimpleThrowable(
  message: String,
  `type`: String,
  stacktrace: Option[String] = None,
  cause: Option[String] = None
)

object FrameworkException {

  def apply(error: DecodingFailure): FrameworkDecodingFailure =
    new FrameworkDecodingFailure(error.message, error.toString())

  def apply(error: ParsingFailure): FrameworkDecodingFailure =
    new FrameworkDecodingFailure(error.message, error.toString())

  def apply(message: String, throwable: Throwable): FrameworkException =
    new GenericFrameworkException(
      message = message, //`type`=throwable.getClass.getSimpleName,
      stacktrace = Some(ThrowableUtils.stackTraceToString(throwable)),
      error = Option(throwable.getCause).map(_.toString).getOrElse(message)
    )

  val exceptionEncoder: Encoder[Exception] =
    Encoder.instance[Exception](t => exceptionJson(t))

  val throwableEncoder: Encoder[Throwable] =
    Encoder.instance[Throwable](t => exceptionJson(t))

  val throwableDecoder: Decoder[Throwable] = Decoder.instance[Throwable] { json =>
    json.as[SimpleThrowable].map(t => new Throwable(t.message))
  }

  final def exceptionJson(t: Throwable): Json = exceptionFields(t).asJson

  private def exceptionFields(t: Throwable): Map[String, String] = {
    val base = Map(
      "message" -> t.getMessage,
      "type" -> t.getClass.getSimpleName,
      "stacktrace" -> ThrowableUtils.stackTraceToString(t)
    )
    base.++(
      Option(t.getCause).map(cause => Map("cause" -> cause.getMessage)).getOrElse(Map())
    )
  }

  implicit final val decodeFrameworkException: Decoder[FrameworkException] =
    Decoder.instance { c =>
      c.as[GenericFrameworkException]

    }

//  implicit final val encodeFrameworkException: Encoder[FrameworkException] = {
//    Encoder.instance {
//      case o: GenericFrameworkException => o.asJson
//      case o: InvalidCredentialsException => o.asJson
//      case o: UnhandledFrameworkException => o.asJson
//      case o: FrameworkDecodingFailure => o.asJson
//      case o: ConfigurationException => o.asJson
//      case o: DBUpdateException => o.asJson
//      case o: MetamodelException => o.asJson
//      case o: SchemaFetchingException => o.asJson
//      case o: MissingSchemaException => o.asJson
//      case o: MissingRecordException => o.asJson
//      case o: MissingFieldException => o.asJson
//      case o: RecordProcessingException => o.asJson
//      case o: AlreadyExistsException => o.asJson
//      case o: ClusterBlockException => o.asJson
//      case o: DocumentAlreadyExistsEngineException => o.asJson
//      case o: DocumentAlreadyExistsException => o.asJson
//      case o: ElasticSearchDeleteException => o.asJson
//      case o: ElasticSearchIllegalStateException => o.asJson
//      case o: ElasticSearchParsingException => o.asJson
//      case o: ElasticSearchQueryException => o.asJson
//      case o: ElasticSearchScriptException => o.asJson
//      case o: ElasticSearchSearchIllegalArgumentException => o.asJson
//      case o: IndexAlreadyExistsException => o.asJson
//      case o: IndexNotFoundException => o.asJson
//      case o: InvalidParameter => o.asJson
//      case o: InvalidParameterQuery => o.asJson
//      case o: InvalidQuery => o.asJson
//      case o: InvalidValueException => o.asJson
//      case o: MappedFieldNotFoundException => o.asJson
//      case o: MapperParsingException => o.asJson
//      case o: MissingValueException => o.asJson
//      case o: MultiDocumentException => o.asJson
//      case o: NoServerAvailableException => o.asJson
//      case o: NotFound => o.asJson
//      case o: NotFoundException => o.asJson
//      case o: NotUniqueValueException => o.asJson
//      case o: QueryError => o.asJson
//      case o: QueryParameterError => o.asJson
//      case o: QueueClosedException => o.asJson
//      case o: QueueDroppedException => o.asJson
//      case o: QueueSearchException => o.asJson
//      case o: ReduceSearchPhaseException => o.asJson
//      case o: ReplicationShardOperationFailedException => o.asJson
//      case o: ScriptFieldsError => o.asJson
//      case o: SearchPhaseExecutionException => o.asJson
//      case o: TypeMissingException => o.asJson
//      case o: VersionConflictEngineException => o.asJson
//      case o: InvalidParameter => o.asJson
//      case o: InvalidValueException => o.asJson
//      case o: WriteException => o.asJson
//      case o: FrameworkMultipleExceptions => o.asJson
//      case o: NoTypeParser => o.asJson
//      case o: ScriptingEngineNotFound => o.asJson
//      case o: MissingScriptException => o.asJson
//      case o: ValidationException => o.asJson
//      case o: UserNotFoundException => o.asJson
//      case o: CloudStorageException => o.asJson
//      case o: DataStorageUndefinedException => o.asJson
//      case o: FrameworkParsingFailure => o.asJson
//      case o: InvalidStorageTypeException => o.asJson
//      case o: MergeMappingException => o.asJson
//      case o: ConverterException => o.asJson
//    }
//  }
}

/**
 * This class defines a GenericFrameworkException entity
 * @param error a string representing the error
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class GenericFrameworkException(
  error: String,
  message: String,
  errorType: ErrorType = ErrorType.UnknownError,
  errorCode: String = "framework.generic",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends FrameworkException

@JsonCodec
final case class InvalidCredentialsException(
  error: String,
  message: String = "credential",
  errorType: ErrorType = ErrorType.UnknownError,
  errorCode: String = "auth.generic",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends FrameworkException

/**
 * This class defines a UnhandledFrameworkException entity
 * @param error a string representing the error
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class UnhandledFrameworkException(
  error: String,
  message: String,
  errorType: ErrorType = ErrorType.UnknownError,
  errorCode: String = "framework.generic",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends FrameworkException

/**
 * This class defines a FrameworkDecodingFailure entity
 * @param error a string representing the error
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class FrameworkDecodingFailure(
  error: String,
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.decoding",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends FrameworkException

/**
 * This class defines a ConfigurationSourceException entity
 * @param error a string representing the error
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class FrameworkParsingFailure(
  error: String,
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "framework.jsonparsing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends FrameworkException

/**
 * This class defines a ConfigurationException entity
 * @param error a string representing the error
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class ConfigurationException(
  error: String,
  message: String,
  errorType: ErrorType = ErrorType.ConfigurationError,
  errorCode: String = "framework.configuration",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends FrameworkException

/**
 * This class defines a DBUpdateException entity
 * @param error a string representing the error
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class DBUpdateException(
  error: String,
  errorType: ErrorType = ErrorType.ProcessingError,
  errorCode: String = "framework.processing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends FrameworkException {
  def message: String = error
}

/**
 * This class defines a DBUpdateException entity
 * @param error a string representing the error
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class CloudStorageException(
  error: String,
  errorType: ErrorType = ErrorType.ServiceError,
  errorCode: String = "framework.processing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends FrameworkException {
  def message: String = error
}

/**
 * This class defines a ConverterException entity
 * @param error a string representing the error
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class ConverterException(
  error: String,
  errorType: ErrorType = ErrorType.ServiceError,
  errorCode: String = "converter.exception",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends FrameworkException {
  def message: String = error
}

/* Schema */

/**
 * Schema
 * @param error a string representing the error
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class MetamodelException(
  error: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "schema.metamodel",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends FrameworkException {
  override def message: String = error
}

/**
 * Schema
 * @param error a string representing the error
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class SchemaFetchingException(
  error: String,
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "schema.fetching",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends FrameworkException

object SchemaFetchingException {

  def apply(error: String, throwable: Throwable): SchemaFetchingException =
    SchemaFetchingException(
      error = error,
      message = throwable.getMessage,
      stacktrace = Some(ThrowableUtils.stackTraceToString(throwable))
    )
}

/**
 * This class defines a MissingSchemaException entity
 * @param error a string representing the error
 * @param message the error message
 * @param stacktrace the stacktrace of the exception
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 */
@JsonCodec
final case class MissingSchemaException(
  error: String,
  message: String = "Missing Schema",
  stacktrace: Option[String] = None,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "schema.missing",
  status: Int = ErrorCode.NotFound
) extends FrameworkException

/**
 * This class defines a MissingFieldException entity
 * @param error a string representing the error
 * @param message the error message
 * @param stacktrace the stacktrace of the exception
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param status HTTP Error Status
 */
@JsonCodec
final case class MissingFieldException(
  error: String,
  message: String = "Missing Field",
  stacktrace: Option[String] = None,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "schema.missing",
  status: Int = ErrorCode.NotFound
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException

/* ElasticSearch */

@JsonCodec
final case class QueueDroppedException(
  message: String,
  status: Int = ErrorCode.InternalServerError,
  errorType: ErrorType = ErrorType.ProcessingError,
  errorCode: String = "queue.dropped"
) extends FrameworkException

@JsonCodec
final case class QueueClosedException(
  message: String,
  status: Int = ErrorCode.InternalServerError,
  errorType: ErrorType = ErrorType.ProcessingError,
  errorCode: String = "queue.closed"
) extends FrameworkException

@JsonCodec
final case class QueueSearchException(
  message: String,
  status: Int = ErrorCode.InternalServerError,
  errorType: ErrorType = ErrorType.ProcessingError,
  errorCode: String = "queue.search"
) extends FrameworkException

@JsonCodec
final case class MultiDocumentException(
  message: String,
  json: Json = Json.Null,
  status: Int = ErrorCode.InternalServerError,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "record.multiple"
) extends FrameworkException

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
) extends FrameworkException

@JsonCodec
final case class ElasticSearchParsingException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.parsing",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null
) extends FrameworkException

@JsonCodec
final case class ElasticSearchDeleteException(
  message: String,
  status: Int = ErrorCode.InternalServerError,
  errorType: ErrorType = ErrorType.UnknownError,
  errorCode: String = "elasticsearch.delete",
  json: Json = Json.Null
) extends FrameworkException

@JsonCodec
final case class ElasticSearchScriptException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.script",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null
) extends FrameworkException

@JsonCodec
final case class ElasticSearchQueryException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.query",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null
) extends FrameworkException

@JsonCodec
sealed trait ElasticSearchSearchException extends FrameworkException

object ElasticSearchSearchException {

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
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException

@JsonCodec
final case class InvalidStorageTypeException(
  error: String,
  message: String,
  stacktrace: Option[String] = None,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "datastore.missing",
  status: Int = ErrorCode.InternalServerError
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException

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
) extends FrameworkException {}

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
