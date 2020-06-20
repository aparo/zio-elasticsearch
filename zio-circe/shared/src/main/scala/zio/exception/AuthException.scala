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

import io.circe.Decoder.Result
import io.circe._
import io.circe.derivation.annotations._
import io.circe.syntax._

/****************************************
 *  AUTH Exceptions
  ****************************************/
@JsonCodec(Configuration.default.withDiscriminator("type"))
sealed trait AuthException extends FrameworkException {
  override def toJsonObject: JsonObject =
    implicitly[Encoder.AsObject[AuthException]]
      .encodeObject(this)
      .add(FrameworkException.FAMILY, Json.fromString("AuthException"))
}

object AuthException extends ExceptionFamily {
  register("AuthException", this)

  override def decode(c: HCursor): Result[FrameworkException] =
    implicitly[Decoder[AuthException]].apply(c)
}

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

@JsonCodec
final case class InvalidCredentialsException(
  error: String,
  message: String = "credential",
  errorType: ErrorType = ErrorType.UnknownError,
  errorCode: String = "auth.generic",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends AuthException {
  override def toJsonObject: JsonObject = this.asJsonObject
}
