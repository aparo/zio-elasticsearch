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

package zio.auth

import zio.common.ThrowableUtils
import zio.exception._

import zio.json.ast.Json
import zio.json._

/**
 * ************************************** AUTH Exceptions
 */
@jsonDiscriminator("type")
sealed trait AuthException extends FrameworkException {

  override def toJsonWithFamily: Either[String, Json] = for {
    json <- implicitly[JsonEncoder[AuthException]].toJsonAST(this)
    jsonFamily <- addFamily(json, "AuthException")
  } yield jsonFamily
}

object AuthException extends ExceptionFamily {
  register("AuthException", this)
  implicit final val jsonDecoder: JsonDecoder[AuthException] =
    DeriveJsonDecoder.gen[AuthException]
  implicit final val jsonEncoder: JsonEncoder[AuthException] =
    DeriveJsonEncoder.gen[AuthException]
  implicit final val jsonCodec: JsonCodec[AuthException] = JsonCodec(jsonEncoder, jsonDecoder)

  override def decode(c: Json): Either[String, FrameworkException] =
    implicitly[JsonDecoder[AuthException]].fromJsonAST(c)

  def apply(throwable: Throwable): AuthException =
    throwable match {
      case e: AuthException =>
        // passthroughs
        e
      case _ =>
        AuthBadResponseException(
          message = throwable.getMessage, //`type`=throwable.getClass.getSimpleName,
          stacktrace = Some(ThrowableUtils.stackTraceToString(throwable)),
          error = Option(throwable.getCause).map(_.toString).getOrElse(throwable.getMessage)
        )

    }

}

/**
 * This class defines a MissingCredentialsException entity
 *
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 */
case class MissingCredentialsException(
  message: String = "auth.credentials",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.NotFound,
  stacktrace: Option[String] = None
) extends AuthException
object MissingCredentialsException {
  implicit final val jsonDecoder: JsonDecoder[MissingCredentialsException] =
    DeriveJsonDecoder.gen[MissingCredentialsException]
  implicit final val jsonEncoder: JsonEncoder[MissingCredentialsException] =
    DeriveJsonEncoder.gen[MissingCredentialsException]
  implicit final val jsonCodec: JsonCodec[MissingCredentialsException] = JsonCodec(jsonEncoder, jsonDecoder)
}

/**
 * This class defines a AuthUUIDException entity
 *
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 */
case class AuthUUIDException(
  message: String = "auth.uuid",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.NotFound,
  stacktrace: Option[String] = None
) extends AuthException
object AuthUUIDException {
  implicit final val jsonDecoder: JsonDecoder[AuthUUIDException] = DeriveJsonDecoder.gen[AuthUUIDException]
  implicit final val jsonEncoder: JsonEncoder[AuthUUIDException] = DeriveJsonEncoder.gen[AuthUUIDException]
  implicit final val jsonCodec: JsonCodec[AuthUUIDException] = JsonCodec(jsonEncoder, jsonDecoder)
}

/**
 * This class defines a UnauthorizedException entity
 *
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 */
case class UnauthorizedException(
  message: String = "auth.error",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.NotFound,
  stacktrace: Option[String] = None
) extends AuthException
object UnauthorizedException {
  implicit final val jsonDecoder: JsonDecoder[UnauthorizedException] = DeriveJsonDecoder.gen[UnauthorizedException]
  implicit final val jsonEncoder: JsonEncoder[UnauthorizedException] = DeriveJsonEncoder.gen[UnauthorizedException]
  implicit final val jsonCodec: JsonCodec[UnauthorizedException] = JsonCodec(jsonEncoder, jsonDecoder)
}

/**
 * This class defines a UserNotFoundException entity
 * @param userId
 *   an User Id
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 */
case class UserNotFoundException(
  userId: String,
  message: String = "auth.error",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.NotFound,
  stacktrace: Option[String] = None
) extends AuthException
object UserNotFoundException {
  implicit final val jsonDecoder: JsonDecoder[UserNotFoundException] = DeriveJsonDecoder.gen[UserNotFoundException]
  implicit final val jsonEncoder: JsonEncoder[UserNotFoundException] = DeriveJsonEncoder.gen[UserNotFoundException]
  implicit final val jsonCodec: JsonCodec[UserNotFoundException] = JsonCodec(jsonEncoder, jsonDecoder)
}

/**
 * This exception is throw if the permission string is malformed
 * @param permissionString
 *   the permission that is not valid
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 */
case class InvalidPermissionStringException(
  permissionString: String,
  message: String = "auth.error",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.NotFound,
  stacktrace: Option[String] = None
) extends AuthException
object InvalidPermissionStringException {
  implicit final val jsonDecoder: JsonDecoder[InvalidPermissionStringException] =
    DeriveJsonDecoder.gen[InvalidPermissionStringException]
  implicit final val jsonEncoder: JsonEncoder[InvalidPermissionStringException] =
    DeriveJsonEncoder.gen[InvalidPermissionStringException]
  implicit final val jsonCodec: JsonCodec[InvalidPermissionStringException] = JsonCodec(jsonEncoder, jsonDecoder)
}

/**
 * This exception is thrown when a property is missing
 * @param userId
 *   an User Id
 * @param property
 *   a property to look for
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 */
case class UserPropertyNotFoundException(
  userId: String,
  property: String,
  message: String = "auth.error",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.NotFound,
  stacktrace: Option[String] = None
) extends AuthException
object UserPropertyNotFoundException {

  implicit final val jsonDecoder: JsonDecoder[UserPropertyNotFoundException] =
    DeriveJsonDecoder.gen[UserPropertyNotFoundException]
  implicit final val jsonEncoder: JsonEncoder[UserPropertyNotFoundException] =
    DeriveJsonEncoder.gen[UserPropertyNotFoundException]
  implicit final val jsonCodec: JsonCodec[UserPropertyNotFoundException] = JsonCodec(jsonEncoder, jsonDecoder)

}

final case class InvalidCredentialsException(
  error: String,
  message: String = "credential",
  errorType: ErrorType = ErrorType.UnknownError,
  errorCode: String = "auth.generic",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.Unauthorized
) extends AuthException
object InvalidCredentialsException {

  implicit final val jsonDecoder: JsonDecoder[InvalidCredentialsException] =
    DeriveJsonDecoder.gen[InvalidCredentialsException]
  implicit final val jsonEncoder: JsonEncoder[InvalidCredentialsException] =
    DeriveJsonEncoder.gen[InvalidCredentialsException]
  implicit final val jsonCodec: JsonCodec[InvalidCredentialsException] = JsonCodec(jsonEncoder, jsonDecoder)

}

final case class AuthBadResponseException(
  error: String,
  message: String = "credential",
  errorType: ErrorType = ErrorType.UnknownError,
  errorCode: String = "auth.response",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.Unauthorized
) extends AuthException
object AuthBadResponseException {

  implicit final val jsonDecoder: JsonDecoder[AuthBadResponseException] =
    DeriveJsonDecoder.gen[AuthBadResponseException]
  implicit final val jsonEncoder: JsonEncoder[AuthBadResponseException] =
    DeriveJsonEncoder.gen[AuthBadResponseException]
  implicit final val jsonCodec: JsonCodec[AuthBadResponseException] = JsonCodec(jsonEncoder, jsonDecoder)

}

/**
 * This exception is thrown when is not possible to generate a JWT token
 *
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 */
case class JWTUnableGenerateTokenException(
  message: String = "auth.error",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.InternalServerError,
  stacktrace: Option[String] = None
) extends AuthException
object JWTUnableGenerateTokenException {

  implicit final val jsonDecoder: JsonDecoder[JWTUnableGenerateTokenException] =
    DeriveJsonDecoder.gen[JWTUnableGenerateTokenException]
  implicit final val jsonEncoder: JsonEncoder[JWTUnableGenerateTokenException] =
    DeriveJsonEncoder.gen[JWTUnableGenerateTokenException]
  implicit final val jsonCodec: JsonCodec[JWTUnableGenerateTokenException] = JsonCodec(jsonEncoder, jsonDecoder)

}

/**
 * This exception is thrown when there is an invalid JWT token
 *
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 */
case class JWTInvalidTokenException(
  message: String = "auth.error",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.InternalServerError,
  stacktrace: Option[String] = None
) extends AuthException
object JWTInvalidTokenException {

  implicit final val jsonDecoder: JsonDecoder[JWTInvalidTokenException] =
    DeriveJsonDecoder.gen[JWTInvalidTokenException]
  implicit final val jsonEncoder: JsonEncoder[JWTInvalidTokenException] =
    DeriveJsonEncoder.gen[JWTInvalidTokenException]
  implicit final val jsonCodec: JsonCodec[JWTInvalidTokenException] = JsonCodec(jsonEncoder, jsonDecoder)

}

/**
 * This exception is thrown when there is unable to parse aJWT token
 *
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 */
case class JWTTokenParsingException(
  message: String = "auth.error",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.InternalServerError,
  stacktrace: Option[String] = None
) extends AuthException

object JWTTokenParsingException {

  implicit final val jsonDecoder: JsonDecoder[JWTTokenParsingException] =
    DeriveJsonDecoder.gen[JWTTokenParsingException]
  implicit final val jsonEncoder: JsonEncoder[JWTTokenParsingException] =
    DeriveJsonEncoder.gen[JWTTokenParsingException]
  implicit final val jsonCodec: JsonCodec[JWTTokenParsingException] = JsonCodec(jsonEncoder, jsonDecoder)

}

case class JWTTokenSignException(
  message: String = "auth.error",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.InternalServerError,
  stacktrace: Option[String] = None
) extends AuthException
object JWTTokenSignException {
  implicit final val jsonDecoder: JsonDecoder[JWTTokenSignException] = DeriveJsonDecoder.gen[JWTTokenSignException]
  implicit final val jsonEncoder: JsonEncoder[JWTTokenSignException] = DeriveJsonEncoder.gen[JWTTokenSignException]
  implicit final val jsonCodec: JsonCodec[JWTTokenSignException] = JsonCodec(jsonEncoder, jsonDecoder)
}

/**
 * This exception is thrown when there is unable to validate a password
 *
 * @param message
 *   the error message
 * @param errorType
 *   the errorType
 * @param errorCode
 *   a string grouping common application errors
 * @param status
 *   HTTP Error Status
 */
case class JWTPasswordException(
  message: String = "auth.error",
  errorType: ErrorType = ErrorType.AuthError,
  errorCode: String = "auth.error",
  status: Int = ErrorCode.InternalServerError,
  stacktrace: Option[String] = None
) extends AuthException
object JWTPasswordException {
  implicit final val jsonDecoder: JsonDecoder[JWTPasswordException] = DeriveJsonDecoder.gen[JWTPasswordException]
  implicit final val jsonEncoder: JsonEncoder[JWTPasswordException] = DeriveJsonEncoder.gen[JWTPasswordException]
  implicit final val jsonCodec: JsonCodec[JWTPasswordException] = JsonCodec(jsonEncoder, jsonDecoder)
}
