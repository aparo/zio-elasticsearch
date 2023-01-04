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

// we move the exception in this package to simplify every access using app.exception._
import zio.elasticsearch.responses.{ BulkResponse, ErrorResponse }

import zio.json._
import zio.json.ast.Json

/**
 * ************************************** Elasticsearch Exceptions
 */
sealed trait ElasticSearchSearchException extends FrameworkException {

  override def toJsonWithFamily: Either[String, Json] = for {
    json <- implicitly[JsonEncoder[ElasticSearchSearchException]].toJsonAST(this)
    jsonFamily <- addFamily(json, "ElasticSearchSearchException")
  } yield jsonFamily

}

object ElasticSearchSearchException extends ExceptionFamily {
  register("ElasticSearchSearchException", this)
  implicit final val jsonDecoder: JsonDecoder[ElasticSearchSearchException] =
    DeriveJsonDecoder.gen[ElasticSearchSearchException]
  implicit final val jsonEncoder: JsonEncoder[ElasticSearchSearchException] =
    DeriveJsonEncoder.gen[ElasticSearchSearchException]
  implicit final val jsonCodec: JsonCodec[ElasticSearchSearchException] = JsonCodec(jsonEncoder, jsonDecoder)

  override def decode(c: Json): Either[String, FrameworkException] =
    implicitly[JsonDecoder[ElasticSearchSearchException]].fromJsonAST(c)

//  implicit def convertDecodeError(
//    error: DecodingFailure
//  ): ElasticSearchParsingException =
//    new ElasticSearchParsingException(error.message)

  def apply(msg: String, status: Int, json: Json) =
    new SearchPhaseExecutionException(msg, status, json = json)

  /*
   * Build an error
   */
  def buildException(data: Json, status: Int = ErrorCode.InternalServerError): FrameworkException =
    data match {
      case Json.Null =>
        if (status == 404) new NotFoundException(s"Error $status", json = data)
        else
          new NotFoundException(s"Error $status", json = data) //TODO improve
      case d: Json.Obj =>
        d.as[ErrorResponse] match {
          case Left(ex) =>
            ElasticSearchQueryException(
              d.toJson,
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

final case class MultiDocumentException(
  message: String,
  json: Json = Json.Null,
  status: Int = ErrorCode.InternalServerError,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.multiple",
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object MultiDocumentException {
  implicit val jsonDecoder: JsonDecoder[MultiDocumentException] = DeriveJsonDecoder.gen[MultiDocumentException]
  implicit val jsonEncoder: JsonEncoder[MultiDocumentException] = DeriveJsonEncoder.gen[MultiDocumentException]
}

final case class ElasticSearchIllegalStateException(
  message: String,
  errorType: ErrorType = ErrorType.UnknownError,
  errorCode: String = "elasticsearch.error",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object ElasticSearchIllegalStateException {
  implicit val jsonDecoder: JsonDecoder[ElasticSearchIllegalStateException] =
    DeriveJsonDecoder.gen[ElasticSearchIllegalStateException]
  implicit val jsonEncoder: JsonEncoder[ElasticSearchIllegalStateException] =
    DeriveJsonEncoder.gen[ElasticSearchIllegalStateException]
}

final case class ElasticSearchParsingException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.parsing",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object ElasticSearchParsingException {
  implicit val jsonDecoder: JsonDecoder[ElasticSearchParsingException] =
    DeriveJsonDecoder.gen[ElasticSearchParsingException]
  implicit val jsonEncoder: JsonEncoder[ElasticSearchParsingException] =
    DeriveJsonEncoder.gen[ElasticSearchParsingException]
}

final case class ElasticSearchDeleteException(
  message: String,
  status: Int = ErrorCode.InternalServerError,
  errorType: ErrorType = ErrorType.UnknownError,
  errorCode: String = "elasticsearch.delete",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object ElasticSearchDeleteException {
  implicit val jsonDecoder: JsonDecoder[ElasticSearchDeleteException] =
    DeriveJsonDecoder.gen[ElasticSearchDeleteException]
  implicit val jsonEncoder: JsonEncoder[ElasticSearchDeleteException] =
    DeriveJsonEncoder.gen[ElasticSearchDeleteException]
}

final case class ElasticSearchScriptException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.script",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object ElasticSearchScriptException {
  implicit val jsonDecoder: JsonDecoder[ElasticSearchScriptException] =
    DeriveJsonDecoder.gen[ElasticSearchScriptException]
  implicit val jsonEncoder: JsonEncoder[ElasticSearchScriptException] =
    DeriveJsonEncoder.gen[ElasticSearchScriptException]
}

final case class ElasticSearchQueryException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.query",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object ElasticSearchQueryException {
  implicit val jsonDecoder: JsonDecoder[ElasticSearchQueryException] =
    DeriveJsonDecoder.gen[ElasticSearchQueryException]
  implicit val jsonEncoder: JsonEncoder[ElasticSearchQueryException] =
    DeriveJsonEncoder.gen[ElasticSearchQueryException]
}
final case class InvalidQueryException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object InvalidQueryException {
  implicit val jsonDecoder: JsonDecoder[InvalidQueryException] = DeriveJsonDecoder.gen[InvalidQueryException]
  implicit val jsonEncoder: JsonEncoder[InvalidQueryException] = DeriveJsonEncoder.gen[InvalidQueryException]
}

final case class InvalidParameterQueryException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object InvalidParameterQueryException {
  implicit val jsonDecoder: JsonDecoder[InvalidParameterQueryException] =
    DeriveJsonDecoder.gen[InvalidParameterQueryException]
  implicit val jsonEncoder: JsonEncoder[InvalidParameterQueryException] =
    DeriveJsonEncoder.gen[InvalidParameterQueryException]
}

final case class QueryException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object QueryException {
  implicit val jsonDecoder: JsonDecoder[QueryException] = DeriveJsonDecoder.gen[QueryException]
  implicit val jsonEncoder: JsonEncoder[QueryException] = DeriveJsonEncoder.gen[QueryException]
}

final case class QueryParameterException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object QueryParameterException {
  implicit val jsonDecoder: JsonDecoder[QueryParameterException] = DeriveJsonDecoder.gen[QueryParameterException]
  implicit val jsonEncoder: JsonEncoder[QueryParameterException] = DeriveJsonEncoder.gen[QueryParameterException]
}

final case class ScriptFieldsException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object ScriptFieldsException {
  implicit val jsonDecoder: JsonDecoder[ScriptFieldsException] = DeriveJsonDecoder.gen[ScriptFieldsException]
  implicit val jsonEncoder: JsonEncoder[ScriptFieldsException] = DeriveJsonEncoder.gen[ScriptFieldsException]
}

/**
 * This class defines a InvalidParameter entity
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
final case class InvalidParameterException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidparameter",
  status: Int = ErrorCode.BadRequest,
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object InvalidParameterException {
  implicit val jsonDecoder: JsonDecoder[InvalidParameterException] = DeriveJsonDecoder.gen[InvalidParameterException]
  implicit val jsonEncoder: JsonEncoder[InvalidParameterException] = DeriveJsonEncoder.gen[InvalidParameterException]
}

final case class MergeMappingException(
  message: String,
  solution: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.mapping",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object MergeMappingException {
  implicit val jsonDecoder: JsonDecoder[MergeMappingException] = DeriveJsonDecoder.gen[MergeMappingException]
  implicit val jsonEncoder: JsonEncoder[MergeMappingException] = DeriveJsonEncoder.gen[MergeMappingException]
}
final case class ElasticSearchSearchIllegalArgumentException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invaliddata",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object ElasticSearchSearchIllegalArgumentException {
  implicit val jsonDecoder: JsonDecoder[ElasticSearchSearchIllegalArgumentException] =
    DeriveJsonDecoder.gen[ElasticSearchSearchIllegalArgumentException]
  implicit val jsonEncoder: JsonEncoder[ElasticSearchSearchIllegalArgumentException] =
    DeriveJsonEncoder.gen[ElasticSearchSearchIllegalArgumentException]
}

final case class IndexNotFoundException(
  message: String,
  status: Int = 404,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.missing",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object IndexNotFoundException {
  implicit val jsonDecoder: JsonDecoder[IndexNotFoundException] = DeriveJsonDecoder.gen[IndexNotFoundException]
  implicit val jsonEncoder: JsonEncoder[IndexNotFoundException] = DeriveJsonEncoder.gen[IndexNotFoundException]
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
final case class IndexAlreadyExistsException(
  message: String,
  status: Int = 409,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.exists",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object IndexAlreadyExistsException {
  implicit val jsonDecoder: JsonDecoder[IndexAlreadyExistsException] =
    DeriveJsonDecoder.gen[IndexAlreadyExistsException]
  implicit val jsonEncoder: JsonEncoder[IndexAlreadyExistsException] =
    DeriveJsonEncoder.gen[IndexAlreadyExistsException]
}

final case class SearchPhaseExecutionException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.search",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object SearchPhaseExecutionException {
  implicit val jsonDecoder: JsonDecoder[SearchPhaseExecutionException] =
    DeriveJsonDecoder.gen[SearchPhaseExecutionException]
  implicit val jsonEncoder: JsonEncoder[SearchPhaseExecutionException] =
    DeriveJsonEncoder.gen[SearchPhaseExecutionException]
}

final case class ReplicationShardOperationFailedException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.search",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object ReplicationShardOperationFailedException {
  implicit val jsonDecoder: JsonDecoder[ReplicationShardOperationFailedException] =
    DeriveJsonDecoder.gen[ReplicationShardOperationFailedException]
  implicit val jsonEncoder: JsonEncoder[ReplicationShardOperationFailedException] =
    DeriveJsonEncoder.gen[ReplicationShardOperationFailedException]
}

final case class ClusterBlockException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.cluster",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object ClusterBlockException {
  implicit val jsonDecoder: JsonDecoder[ClusterBlockException] = DeriveJsonDecoder.gen[ClusterBlockException]
  implicit val jsonEncoder: JsonEncoder[ClusterBlockException] = DeriveJsonEncoder.gen[ClusterBlockException]
}

final case class MapperParsingException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.mapping",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object MapperParsingException {
  implicit val jsonDecoder: JsonDecoder[MapperParsingException] = DeriveJsonDecoder.gen[MapperParsingException]
  implicit val jsonEncoder: JsonEncoder[MapperParsingException] = DeriveJsonEncoder.gen[MapperParsingException]
}

final case class ReduceSearchPhaseException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.search",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object ReduceSearchPhaseException {
  implicit val jsonDecoder: JsonDecoder[ReduceSearchPhaseException] = DeriveJsonDecoder.gen[ReduceSearchPhaseException]
  implicit val jsonEncoder: JsonEncoder[ReduceSearchPhaseException] = DeriveJsonEncoder.gen[ReduceSearchPhaseException]
}

final case class TypeMissingException(
  message: String,
  status: Int = 404,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.type",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object TypeMissingException {
  implicit val jsonDecoder: JsonDecoder[TypeMissingException] = DeriveJsonDecoder.gen[TypeMissingException]
  implicit val jsonEncoder: JsonEncoder[TypeMissingException] = DeriveJsonEncoder.gen[TypeMissingException]
}

//mappings

final case class MappedFieldNotFoundException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.mapping",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object MappedFieldNotFoundException {
  implicit val jsonDecoder: JsonDecoder[MappedFieldNotFoundException] =
    DeriveJsonDecoder.gen[MappedFieldNotFoundException]
  implicit val jsonEncoder: JsonEncoder[MappedFieldNotFoundException] =
    DeriveJsonEncoder.gen[MappedFieldNotFoundException]
}
final case class BulkException(
  bulkResult: BulkResponse,
  message: String = "Bulk exception in processing some records",
  status: Int = 500,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.bulk",
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends ElasticSearchSearchException
object BulkException {
  implicit val jsonDecoder: JsonDecoder[BulkException] = DeriveJsonDecoder.gen[BulkException]
  implicit val jsonEncoder: JsonEncoder[BulkException] = DeriveJsonEncoder.gen[BulkException]
}
