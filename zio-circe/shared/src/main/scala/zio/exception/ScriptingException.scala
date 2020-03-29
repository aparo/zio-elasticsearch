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
import io.circe.syntax._
import io.circe.derivation.annotations._

@JsonCodec(Configuration.default.withDiscriminator("type"))
sealed trait ScriptingException extends FrameworkException {
  override def toJsonObject: JsonObject =
    implicitly[Encoder.AsObject[ScriptingException]]
      .encodeObject(this)
      .add(FrameworkException.FAMILY, Json.fromString("ScriptingException"))
}

object ScriptingException extends ExceptionFamily {
  register("ScriptingException", this)

  override def decode(c: HCursor): Result[FrameworkException] = implicitly[Decoder[ScriptingException]].apply(c)
}

/**
 * This class defines a ScriptingEngineNotFound entity
 *
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class ScriptingEngineNotFoundException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "scripting.missing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends ScriptingException {
  override def toJsonObject: JsonObject = this.asJsonObject
}

/**
 * This class defines a MissingScriptException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class MissingScriptException(
  message: String,
  errorType: ErrorType = ErrorType.ValidationError,
  errorCode: String = "scripting.missing",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.NotFound
) extends ScriptingException {
  override def toJsonObject: JsonObject = this.asJsonObject
}
