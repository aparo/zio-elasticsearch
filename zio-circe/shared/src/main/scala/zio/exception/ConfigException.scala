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
sealed trait ConfigException extends FrameworkException {
  override def toJsonObject: JsonObject =
    implicitly[Encoder.AsObject[ConfigException]]
      .encodeObject(this)
      .add(FrameworkException.FAMILY, Json.fromString("ConfigException"))
}

object ConfigException extends ExceptionFamily {
  register("ConfigException", this)

  override def decode(c: HCursor): Result[FrameworkException] =
    implicitly[Decoder[ConfigException]].apply(c)
}

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
) extends ConfigException {
  override def toJsonObject: JsonObject = this.asJsonObject
}

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
) extends ConfigException {
  override def toJsonObject: JsonObject = this.asJsonObject
}
