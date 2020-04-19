/*
 * Copyright 2019-2020 Alberto Paro
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

import io.circe._
import io.circe.derivation.annotations.JsonCodec
import io.circe.syntax._
import zio.common.ThrowableUtils

trait FrameworkException extends Throwable {
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

  def toJsonObject: JsonObject

  def toJson: Json = Json.fromJsonObject(toJsonObject)

  def toErrorJson: Json =
    Json.obj("status" -> Json.fromInt(status),
             "message" -> Json.fromString(message))

  def toGeneric: GenericFrameworkException =
    GenericFrameworkException(error = errorCode, message = message)

  protected def addType(obj: JsonObject, canonicalType: String): JsonObject =
    obj.add("type", Json.fromString(canonicalType.split('.').last))
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

trait ExceptionFamily {
  def register(name: String, family: ExceptionFamily): Unit =
    FrameworkException.addExceptionFamily(name, family)

  def decode(c: HCursor): Decoder.Result[FrameworkException]
}

object FrameworkException {
  // key for all the derivated sealed
  val FAMILY = "_family"
  private var families = Map.empty[String, ExceptionFamily]

  def addExceptionFamily(name: String, family: ExceptionFamily): Unit =
    families += (name -> family)

  def apply(error: DecodingFailure): DecodingFailureException =
    new DecodingFailureException(error.message, error.toString())

  def apply(error: ParsingFailure): DecodingFailureException =
    new DecodingFailureException(error.message, error.toString())

  def apply(message: String, throwable: Throwable): FrameworkException =
    new GenericFrameworkException(
      message = message, //`type`=throwable.getClass.getSimpleName,
      stacktrace = Some(ThrowableUtils.stackTraceToString(throwable)),
      error = Option(throwable.getCause).map(_.toString).getOrElse(message)
    )

  def apply(throwable: Throwable): FrameworkException =
    new GenericFrameworkException(
      message = throwable.getMessage, //`type`=throwable.getClass.getSimpleName,
      stacktrace = Some(ThrowableUtils.stackTraceToString(throwable)),
      error = Option(throwable.getCause)
        .map(_.toString)
        .getOrElse(throwable.getMessage)
    )

  val exceptionEncoder: Encoder[Exception] =
    Encoder.instance[Exception](t => exceptionJson(t))

  val throwableEncoder: Encoder[Throwable] =
    Encoder.instance[Throwable](t => exceptionJson(t))

  val throwableDecoder: Decoder[Throwable] = Decoder.instance[Throwable] {
    json =>
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
      Option(t.getCause)
        .map(cause => Map("cause" -> cause.getMessage))
        .getOrElse(Map())
    )
  }

  implicit final val decodeFrameworkException: Decoder[FrameworkException] =
    Decoder.instance { c =>
      c.get[String](FAMILY) match {
        case Left(_) =>
          c.get[String]("type") match {
            case Left(_) =>
              c.as[GenericFrameworkException]
            case Right(typ) =>
              typ match {
                case "GenericFrameworkException" =>
                  c.as[GenericFrameworkException]
                case "UnhandledFrameworkException" =>
                  c.as[UnhandledFrameworkException]
                case "FrameworkMultipleExceptions" =>
                  c.as[FrameworkMultipleExceptions]
                case "ValidationErrorException" =>
                  c.as[ValidationErrorException]
                case _ => c.as[GenericFrameworkException]

              }
          }

        case Right(family) =>
          families.get(family) match {
            case Some(familyDecoder) => familyDecoder.decode(c)
            case None =>
              Left(
                DecodingFailure(s"Not registered exception family: $family",
                                Nil))
          }
      }

    }

  implicit final val encodeObjectFrameworkException
    : Encoder.AsObject[FrameworkException] =
    Encoder.AsObject.instance(_.toJsonObject)
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
) extends FrameworkException {
  override def toJsonObject: JsonObject =
    addType(this.asJsonObject, this.getClass.getCanonicalName)
}

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
) extends FrameworkException {
  override def toJsonObject: JsonObject =
    addType(this.asJsonObject, this.getClass.getCanonicalName)
}

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
) extends FrameworkException {
  override def toJsonObject: JsonObject =
    addType(this.asJsonObject, this.getClass.getCanonicalName)
}

object FrameworkMultipleExceptions {

  def apply(exceptions: Seq[FrameworkException]): FrameworkMultipleExceptions =
    new FrameworkMultipleExceptions(exceptions.map(_.message).mkString("\n"),
                                    exceptions = exceptions.map(_.toGeneric))
}

@JsonCodec
final case class ValidationErrorException(
    field: String,
    message: String,
    status: Int = ErrorCode.InternalServerError,
    errorType: ErrorType = ErrorType.ValidationError,
    errorCode: String = "validation.error"
) extends FrameworkException {
  override def toJsonObject: JsonObject =
    addType(this.asJsonObject, this.getClass.getCanonicalName)
}
