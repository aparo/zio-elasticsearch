/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import enumeratum.EnumEntry.Lowercase
import io.circe.derivation.annotations.JsonCodec

sealed trait RegexOption extends EnumEntry with Lowercase

object RegexOption extends Enum[RegexOption] with CirceEnum[RegexOption] {

  case object CANON_EQ extends RegexOption

  case object CASE_INSENSITIVE extends RegexOption

  case object COMMENTS extends RegexOption

  case object DOTALL extends RegexOption

  case object LITERAL extends RegexOption
  case object MULTILINE extends RegexOption
  case object UNICODE_CASE extends RegexOption
  case object UNICODE_CHARACTER_CLASS extends RegexOption
  case object UNIX_LINES extends RegexOption

  val values = findValues

}

@JsonCodec
final case class Regex(pattern: String, flags: Seq[RegexOption] = Nil)
