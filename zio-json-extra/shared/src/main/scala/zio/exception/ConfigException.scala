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
import zio.json.ast._
import zio.json._

/**
 * ************************************** Config Exceptions
 */
@jsonDiscriminator("type")
sealed trait ConfigException extends FrameworkException {
  override def toJsonWithFamily: Either[String, Json] = for {
    json <- implicitly[JsonEncoder[ConfigException]].toJsonAST(this)
    jsonFamily <- addFamily(json, "ConfigException")
  } yield jsonFamily
}

object ConfigException extends ExceptionFamily {
  register("ConfigException", this)
  implicit final val jsonDecoder: JsonDecoder[ConfigException] =
    DeriveJsonDecoder.gen[ConfigException]
  implicit final val jsonEncoder: JsonEncoder[ConfigException] =
    DeriveJsonEncoder.gen[ConfigException]
  implicit final val jsonCodec: JsonCodec[ConfigException] = JsonCodec(jsonEncoder, jsonDecoder)

  override def decode(c: Json): Either[String, FrameworkException] =
    implicitly[JsonDecoder[ConfigException]].fromJsonAST(c)

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
final case class PropertyNotFoundException(
  message: String,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "config.error",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends ConfigException
object PropertyNotFoundException {
  implicit val jsonDecoder: JsonDecoder[PropertyNotFoundException] = DeriveJsonDecoder.gen[PropertyNotFoundException]
  implicit val jsonEncoder: JsonEncoder[PropertyNotFoundException] = DeriveJsonEncoder.gen[PropertyNotFoundException]
}

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
  implicit val jsonDecoder: JsonDecoder[ConfigurationException] = DeriveJsonDecoder.gen[ConfigurationException]
  implicit val jsonEncoder: JsonEncoder[ConfigurationException] = DeriveJsonEncoder.gen[ConfigurationException]
}
