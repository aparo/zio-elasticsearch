/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.elasticsearch.common.analysis

import zio.json._

sealed trait PhoneticLanguage

object PhoneticLanguage {

  case object any extends PhoneticLanguage

  case object common extends PhoneticLanguage

  case object cyrillic extends PhoneticLanguage

  case object english extends PhoneticLanguage

  case object french extends PhoneticLanguage

  case object german extends PhoneticLanguage

  case object hebrew extends PhoneticLanguage

  case object hungarian extends PhoneticLanguage

  case object polish extends PhoneticLanguage

  case object romanian extends PhoneticLanguage

  case object russian extends PhoneticLanguage

  case object spanish extends PhoneticLanguage

  implicit final val decoder: JsonDecoder[PhoneticLanguage] =
    DeriveJsonDecoderEnum.gen[PhoneticLanguage]
  implicit final val encoder: JsonEncoder[PhoneticLanguage] =
    DeriveJsonEncoderEnum.gen[PhoneticLanguage]
  implicit final val codec: JsonCodec[PhoneticLanguage] =
    JsonCodec(encoder, decoder)

}
