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

import zio.json.ast.Json
import zio.json._

@jsonDiscriminator("type")
sealed trait ScriptingException extends FrameworkException {
  override def toJsonWithFamily: Either[String, Json] = for {
    json <- implicitly[JsonEncoder[ScriptingException]].toJsonAST(this)
    jsonFamily <- addFamily(json, "ScriptingException")
  } yield jsonFamily
}

object ScriptingException extends ExceptionFamily {
  register("ScriptingException", this)
  implicit final val jsonDecoder: JsonDecoder[ScriptingException] =
    DeriveJsonDecoder.gen[ScriptingException]
  implicit final val jsonEncoder: JsonEncoder[ScriptingException] =
    DeriveJsonEncoder.gen[ScriptingException]
  implicit final val jsonCodec: JsonCodec[ScriptingException] = JsonCodec(jsonEncoder, jsonDecoder)
  override def decode(c: Json): Either[String, FrameworkException] =
    implicitly[JsonDecoder[ScriptingException]].fromJsonAST(c)
}

/**
 * This class defines a ScriptingEngineNotFound entity
 *
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
final case class ScriptingEngineNotFoundException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "scripting.missing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends ScriptingException
object ScriptingEngineNotFoundException {
  implicit final val jsonDecoder: JsonDecoder[ScriptingEngineNotFoundException] =
    DeriveJsonDecoder.gen[ScriptingEngineNotFoundException]
  implicit final val jsonEncoder: JsonEncoder[ScriptingEngineNotFoundException] =
    DeriveJsonEncoder.gen[ScriptingEngineNotFoundException]
  implicit final val jsonCodec: JsonCodec[ScriptingEngineNotFoundException] = JsonCodec(jsonEncoder, jsonDecoder)
}

/**
 * This class defines a MissingScriptException entity
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
final case class MissingScriptException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "scripting.missing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends ScriptingException
object MissingScriptException {
  implicit final val jsonDecoder: JsonDecoder[MissingScriptException] =
    DeriveJsonDecoder.gen[MissingScriptException]
  implicit final val jsonEncoder: JsonEncoder[MissingScriptException] =
    DeriveJsonEncoder.gen[MissingScriptException]
  implicit final val jsonCodec: JsonCodec[MissingScriptException] = JsonCodec(jsonEncoder, jsonDecoder)
}
