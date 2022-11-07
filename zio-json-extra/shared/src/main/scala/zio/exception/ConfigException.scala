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
import io.circe.JsonDecoder.Result
import zio.json.ast.Json
import zio.json._
import io.circe.derivation.annotations._

/**
 * ************************************** Config Exceptions
 */
@jsonDiscriminator("type")
sealed trait ConfigException extends FrameworkException {
  override def toJsonObject: Json.Obj =
    implicitly[JsonEncoder[ConfigException]]
      .encodeObject(this)
      .add(FrameworkException.FAMILY, Json.Str("ConfigException"))
}

object ConfigException extends ExceptionFamily {
  register("ConfigException", this)

  override def decode(c: Json): Either[String, FrameworkException] = implicitly[JsonDecoder[ConfigException]].apply(c)
}

/**
 * This class defines a PropertyNotFoundException entity
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
@jsonDerive
final case class PropertyNotFoundException(
  message: String,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "config.error",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends ConfigException

/**
 * This class defines a ConfigurationException entity
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
@jsonDerive
final case class ConfigurationException(
  error: String,
  message: String,
  errorType: ErrorType = ErrorType.ConfigurationError,
  errorCode: String = "framework.configuration",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends ConfigException

object ConfigurationException {
  def apply(message: String): ConfigurationException = ConfigurationException(message, message)
}
