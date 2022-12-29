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

package zio.elasticsearch

import zio.json._
@jsonEnumLowerCase
sealed trait RegexOption extends EnumLowerCase

object RegexOption {

  case object CANON_EQ extends RegexOption

  case object CASE_INSENSITIVE extends RegexOption

  case object COMMENTS extends RegexOption

  case object DOTALL extends RegexOption

  case object LITERAL extends RegexOption
  case object MULTILINE extends RegexOption
  case object UNICODE_CASE extends RegexOption
  case object UNICODE_CHARACTER_CLASS extends RegexOption
  case object UNIX_LINES extends RegexOption

  implicit final val decoder: JsonDecoder[RegexOption] =
    DeriveJsonDecoderEnum.gen[RegexOption]
  implicit final val encoder: JsonEncoder[RegexOption] =
    DeriveJsonEncoderEnum.gen[RegexOption]
  implicit final val codec: JsonCodec[RegexOption] = JsonCodec(encoder, decoder)

}

final case class Regex(pattern: String, flags: Seq[RegexOption] = Nil)
object Regex {
  implicit val jsonDecoder: JsonDecoder[Regex] = DeriveJsonDecoder.gen[Regex]
  implicit val jsonEncoder: JsonEncoder[Regex] = DeriveJsonEncoder.gen[Regex]
}
