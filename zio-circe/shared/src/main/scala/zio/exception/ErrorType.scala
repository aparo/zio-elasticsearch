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

import enumeratum.{CirceEnum, Enum, EnumEntry}

import scala.collection.immutable

sealed trait ErrorType extends EnumEntry

case object ErrorType extends Enum[ErrorType] with CirceEnum[ErrorType] {

  case object AccessDeniedError extends ErrorType

  case object AuthError extends ErrorType

  case object ConfigurationError extends ErrorType

  case object ProcessingError extends ErrorType

  case object ServerError extends ErrorType

  case object ServiceError extends ErrorType

  case object ValidationError extends ErrorType

  case object SchemaError extends ErrorType

  case object UnknownError extends ErrorType

  val values: immutable.IndexedSeq[ErrorType] = findValues
}
