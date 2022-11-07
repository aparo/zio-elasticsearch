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

import zio.Chunk
import zio.common.ThrowableUtils
import zio.json.ast.{ Json, JsonCursor, JsonUtils }
import zio.json._
import zio.json._
import zio.json._

trait FrameworkException extends Throwable {
  def status: Int

  def errorType: ErrorType

  def errorCode: String

  def message: String

  def stacktrace: Option[String]

  /**
   * messageParameters: values to be substituted in the messages file
   */
  def messageParameters: Seq[AnyRef] = Seq.empty

  /**
   * logMap: any key value pair that need to be logged as part of the
   * [[HttpErrorLog]] but is not required to be part of the error response in
   * the [[ErrorEnvelope]]
   */
  def logMap: Map[String, AnyRef] = Map.empty[String, AnyRef]

  /*
   For the most part, exceptions will be logged globally at the outer edges where the logging thread will most likely be the
   dispatcher thread. However, the actual failure might have occurred on a different thread. Hence we capture this information
   as it might be useful in debugging errors.
   */
  val thread: Option[String] = Some(Thread.currentThread().getName)

  def toJsonWithFamily: Json

  def toErrorJson: Json =
    Json.Obj(
      "status" -> Json.Num(status),
      "message" -> Json.Str(message),
      "stacktrace" -> Json.Str(stacktrace.getOrElse("")),
      "error_code" -> Json.Str(errorCode)
    )

  def toJsonStringException: String = toJsonWithFamily.toJson

  override def getMessage: String = message

  def toGeneric: GenericFrameworkException =
    GenericFrameworkException(error = errorCode, message = message, stacktrace = stacktrace)

  protected def addType(eObj: Either[String, Json], canonicalType: String): Json =
    eObj match {
      case Left(_) => Json.Obj()
      case Right(obj) =>
        obj match {
          case Json.Obj(fields) => Json.Obj(fields ++ Chunk("type" -> Json.Str(canonicalType.split('.').last)))
          case _                => obj
        }
    }
}

trait ExceptionFamily {
  def register(name: String, family: ExceptionFamily): Unit =
    FrameworkException.addExceptionFamily(name, family)

  def decode(c: Json): Either[String, FrameworkException]
}

object FrameworkException {
  // key for all the derivated sealed
  val FAMILY = "_family"
  private var families = Map.empty[String, ExceptionFamily]

  def addExceptionFamily(name: String, family: ExceptionFamily): Unit =
    families += (name -> family)

  def fromDecodingFailure(message: String, json: Json): DecodingFailureException =
    new DecodingFailureException(message, json.toString())

//  def fromParsingFailure(message: String, json: Json): DecodingFailureException =
//    new DecodingFailureException(message, json.toString())
//

  def apply(message: String, throwable: Throwable): FrameworkException =
    throwable match {
      case e: FrameworkException =>
        // passthroughs
        new GenericFrameworkException(
          message = message, //`type`=throwable.getClass.getSimpleName,
          stacktrace = e.stacktrace,
          error = Option(throwable.getCause).map(_.toString).getOrElse(message),
          subException = Some(e)
        )
      case _ =>
        new GenericFrameworkException(
          message = message, //`type`=throwable.getClass.getSimpleName,
          stacktrace = Some(ThrowableUtils.stackTraceToString(throwable)),
          error = Option(throwable.getCause).map(_.toString).getOrElse(message)
        )

    }

  def apply(throwable: Throwable): FrameworkException =
    throwable match {
      case e: FrameworkException =>
        // passthroughs
        e
      case _ =>
        GenericFrameworkException(
          message = throwable.getMessage, //`type`=throwable.getClass.getSimpleName,
          stacktrace = Some(ThrowableUtils.stackTraceToString(throwable)),
          error = Option(throwable.getCause).map(_.toString).getOrElse(throwable.getMessage)
        )

    }

  def jsonFailure(message: String): JsonDecodingException=JsonDecodingException(message)

  val exceptionEncoder: JsonEncoder[Exception] = Json.encoder.contramap(t => exceptionJson(t))

  val throwableEncoder: JsonEncoder[Throwable] = Json.encoder.contramap(t => exceptionJson(t))

  val throwableDecoder: JsonDecoder[Throwable] = Json.decoder.mapOrFail(
    _.as[SimpleThrowable].map(t => new Throwable(t.message))
  )

  final def exceptionJson(t: Throwable): Json = exceptionFields(t).toJson.toJsonAST.right.get // very ugly

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

  implicit final val decodeFrameworkException: JsonDecoder[FrameworkException] =
    Json.decoder.mapOrFail { json =>
      json.get(JsonCursor.field(FAMILY)) match {
        case Left(_) =>
          json.get(JsonCursor.field("type")) match {
            case Right(Json.Str(typ)) =>
              typ match {
                case "GenericFrameworkException"   => json.as[GenericFrameworkException]
                case "UnhandledFrameworkException" => json.as[UnhandledFrameworkException]
                case "FrameworkMultipleExceptions" => json.as[FrameworkMultipleExceptions]
                case "ValidationErrorException"    => json.as[ValidationErrorException]
                case "InvalidParameterException"   => json.as[InvalidParameterException]
                case _                             => json.as[GenericFrameworkException]

              }
            case Left(_) =>
              json.as[GenericFrameworkException]
          }

        case Right(Json.Str(family)) =>
          families.get(family) match {
            case Some(familyDecoder) => familyDecoder.decode(json)
            case None                => Left(s"Not registered exception family: $family")
          }
        case Left(_) =>
          json.as[GenericFrameworkException]
      }

    }

  implicit final val encodeObjectFrameworkException: JsonEncoder[FrameworkException] =
    Json.encoder.contramap(_.toJsonWithFamily)
}

/**
 * This class defines a GenericFrameworkException entity
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
final case class GenericFrameworkException(
  error: String,
  message: String,
  errorType: ErrorType = ErrorType.UnknownError,
  errorCode: String = "framework.generic",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError,
  subException: Option[FrameworkException] = None
) extends FrameworkException {
  override def toJsonWithFamily: Json.Obj = addType(this.toJsonAST, "GenericFrameworkException")
}

object GenericFrameworkException {
  implicit final val jsonDecoder: JsonDecoder[GenericFrameworkException] =
    DeriveJsonDecoder.gen[GenericFrameworkException]
  implicit final val jsonEncoder: JsonEncoder[GenericFrameworkException] =
    DeriveJsonEncoder.gen[GenericFrameworkException]
  implicit final val jsonCodec: JsonCodec[GenericFrameworkException] = JsonCodec(jsonEncoder, jsonDecoder)
}

/**
 * This class defines a UnhandledFrameworkException entity
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
final case class UnhandledFrameworkException(
  error: String,
  message: String,
  errorType: ErrorType = ErrorType.UnknownError,
  errorCode: String = "framework.generic",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends FrameworkException {
  override def toJsonWithFamily: Json.Obj = addType(this.toJsonAST, "UnhandledFrameworkException")
}

object UnhandledFrameworkException {
  implicit final val jsonDecoder: JsonDecoder[UnhandledFrameworkException] =
    DeriveJsonDecoder.gen[UnhandledFrameworkException]
  implicit final val jsonEncoder: JsonEncoder[UnhandledFrameworkException] =
    DeriveJsonEncoder.gen[UnhandledFrameworkException]
  implicit final val jsonCodec: JsonCodec[UnhandledFrameworkException] = JsonCodec(jsonEncoder, jsonDecoder)
}

/**
 * This class defines a FrameworkMultipleExceptions entity
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
 * @param exceptions
 *   a list of FrameworkException entities
 */
final case class FrameworkMultipleExceptions(
  message: String,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "framework.exceptions",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError,
  exceptions: Seq[GenericFrameworkException] = Nil
) extends FrameworkException {
  override def toJsonWithFamily: Json.Obj = addType(this.toJsonAST, "FrameworkMultipleExceptions")
}

object FrameworkMultipleExceptions {
  implicit final val jsonDecoder: JsonDecoder[FrameworkMultipleExceptions] =
    DeriveJsonDecoder.gen[FrameworkMultipleExceptions]
  implicit final val jsonEncoder: JsonEncoder[FrameworkMultipleExceptions] =
    DeriveJsonEncoder.gen[FrameworkMultipleExceptions]
  implicit final val jsonCodec: JsonCodec[FrameworkMultipleExceptions] = JsonCodec(jsonEncoder, jsonDecoder)

  def apply(exceptions: Seq[FrameworkException]): FrameworkMultipleExceptions =
    new FrameworkMultipleExceptions(exceptions.map(_.message).mkString("\n"), exceptions = exceptions.map(_.toGeneric))
}

final case class ValidationErrorException(
  field: String,
  message: String,
  status: Int = ErrorCode.InternalServerError,
  stacktrace: Option[String] = None,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "validation.error"
) extends FrameworkException {
  override def toJsonWithFamily: Json.Obj = addType(this.toJsonAST, "ValidationErrorException")
}

object ValidationErrorException {
  implicit final val jsonDecoder: JsonDecoder[ValidationErrorException] =
    DeriveJsonDecoder.gen[ValidationErrorException]
  implicit final val jsonEncoder: JsonEncoder[ValidationErrorException] =
    DeriveJsonEncoder.gen[ValidationErrorException]
  implicit final val jsonCodec: JsonCodec[ValidationErrorException] = JsonCodec(jsonEncoder, jsonDecoder)
}

/**
 * This class defines a InvalidParameter entity
 *
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
  errorCode: String = "framework.invalid_parameter",
  status: Int = ErrorCode.BadRequest,
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends FrameworkException {
  override def toJsonWithFamily: Json.Obj = addType(this.toJsonAST, "InvalidParameterException")
}
object InvalidParameterException {
  implicit final val jsonDecoder: JsonDecoder[InvalidParameterException] =
    DeriveJsonDecoder.gen[InvalidParameterException]
  implicit final val jsonEncoder: JsonEncoder[InvalidParameterException] =
    DeriveJsonEncoder.gen[InvalidParameterException]
  implicit final val jsonCodec: JsonCodec[InvalidParameterException] = JsonCodec(jsonEncoder, jsonDecoder)
}

final case class JsonDecodingException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "json.decode_exception",
  status: Int = ErrorCode.UnprocessableEntity,
  json: Json = Json.Null,
  stacktrace: Option[String] = None
) extends FrameworkException {
  override def toJsonWithFamily: Json.Obj = addType(this.toJsonAST, "JsonDecodingFailure")
}
object JsonDecodingException {
  implicit final val jsonDecoder: JsonDecoder[JsonDecodingException] = DeriveJsonDecoder.gen[JsonDecodingException]
  implicit final val jsonEncoder: JsonEncoder[JsonDecodingException] = DeriveJsonEncoder.gen[JsonDecodingException]
  implicit final val jsonCodec: JsonCodec[JsonDecodingException] = JsonCodec(jsonEncoder, jsonDecoder)
}

/**
 * This class defines a SimpleThrowable entity
 * @param message
 *   the error message
 * @param type
 *   the type of the SimpleThrowable entity
 * @param stacktrace
 *   the stacktrace of the exception
 * @param cause
 *   the cause of the exception
 */
final case class SimpleThrowable(
  message: String,
  `type`: String,
  stacktrace: Option[String] = None,
  cause: Option[String] = None
)

object SimpleThrowable {
  implicit final val jsonDecoder: JsonDecoder[SimpleThrowable] = DeriveJsonDecoder.gen[SimpleThrowable]
  implicit final val jsonEncoder: JsonEncoder[SimpleThrowable] = DeriveJsonEncoder.gen[SimpleThrowable]
  implicit final val jsonCodec: JsonCodec[SimpleThrowable] = JsonCodec(jsonEncoder, jsonDecoder)
}
