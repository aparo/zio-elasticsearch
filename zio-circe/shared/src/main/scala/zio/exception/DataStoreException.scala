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
import io.circe.{ Decoder, Encoder, HCursor, Json, JsonObject }
import io.circe.derivation.annotations.{ Configuration, JsonCodec }

/****************************************
 *  DataStore Exceptions
  ****************************************/
@JsonCodec(Configuration.default.withDiscriminator("type"))
sealed trait DataStoreException extends FrameworkException {
  override def toJsonObject: JsonObject =
    implicitly[Encoder.AsObject[DataStoreException]]
      .encodeObject(this)
      .add(FrameworkException.FAMILY, Json.fromString("DataStoreException"))
}

object DataStoreException extends ExceptionFamily {
  register("DataStoreException", this)

  override def decode(c: HCursor): Result[FrameworkException] =
    implicitly[Decoder[DataStoreException]].apply(c)
}

@JsonCodec
final case class DataStorageUndefinedException(
  message: String,
  stacktrace: Option[String] = None,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "datastore.missing",
  status: Int = ErrorCode.InternalServerError
) extends DataStoreException

@JsonCodec
final case class InvalidStorageTypeException(
  error: String,
  message: String,
  stacktrace: Option[String] = None,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "datastore.missing",
  status: Int = ErrorCode.InternalServerError
) extends DataStoreException

/**
 * This class defines a WriteException entity
 * @param message the error message
 * @param errorType the errorType
 * @param errorCode a string grouping common application errors
 * @param stacktrace the stacktrace of the exception
 * @param status HTTP Error Status
 */
@JsonCodec
final case class WriteException(
  message: String,
  errorType: ErrorType = ErrorType.ServerError,
  errorCode: String = "datastore.write",
  stacktrace: Option[String] = None,
  status: Int = ErrorCode.InternalServerError
) extends DataStoreException
