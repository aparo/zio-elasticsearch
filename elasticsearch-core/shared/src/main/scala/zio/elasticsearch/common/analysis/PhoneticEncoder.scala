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

sealed trait PhoneticEncoder

object PhoneticEncoder {

  case object metaphone extends PhoneticEncoder

  case object double_metaphone extends PhoneticEncoder

  case object soundex extends PhoneticEncoder

  case object refined_soundex extends PhoneticEncoder

  case object caverphone1 extends PhoneticEncoder

  case object caverphone2 extends PhoneticEncoder

  case object cologne extends PhoneticEncoder

  case object nysiis extends PhoneticEncoder

  case object koelnerphonetik extends PhoneticEncoder

  case object haasephonetik extends PhoneticEncoder

  case object beider_morse extends PhoneticEncoder

  case object daitch_mokotoff extends PhoneticEncoder

  implicit final val decoder: JsonDecoder[PhoneticEncoder] =
    DeriveJsonDecoderEnum.gen[PhoneticEncoder]
  implicit final val encoder: JsonEncoder[PhoneticEncoder] =
    DeriveJsonEncoderEnum.gen[PhoneticEncoder]
  implicit final val codec: JsonCodec[PhoneticEncoder] =
    JsonCodec(encoder, decoder)

}
