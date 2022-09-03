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

// we move the exception in this package to simplify every access using zio.exception._

import scala.language.implicitConversions
import io.circe._
import io.circe.derivation.annotations.{ Configuration, JsonCodec }
import elasticsearch.responses.ErrorResponse
import io.circe.Decoder.Result

trait ElasticSearchException extends FrameworkException

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
  override def decode(c: HCursor): Result[FrameworkException] =
    implicitly[Decoder[ElasticSearchSearchException]].apply(c)

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
        if (status == 404) new NotFoundException(s"Error $status", json = data)
        else new NotFoundException(s"Error $status", json = data) //TODO improve
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
  errorCode: String = "elasticsearch.multiple"
) extends ElasticSearchSearchException

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
@JsonCodec
final case class InvalidQueryException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class InvalidParameterQueryException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  status: Int = ErrorCode.InternalServerError,
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class QueryException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class QueryParameterException(
  message: String,
  status: Int,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidquery",
  json: Json = Json.Null
) extends ElasticSearchSearchException

@JsonCodec
final case class ScriptFieldsException(
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
final case class InvalidParameterException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "elasticsearch.invalidparameter",
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
