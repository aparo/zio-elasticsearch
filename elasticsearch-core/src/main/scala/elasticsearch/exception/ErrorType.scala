/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.exception

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

  case object UnknownError extends ErrorType

  val values: immutable.IndexedSeq[ErrorType] = findValues
}
