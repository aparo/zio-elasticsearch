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

sealed trait PhoneticNameType

object PhoneticNameType {

  case object generic extends PhoneticNameType

  case object ashkenazi extends PhoneticNameType

  case object sephardic extends PhoneticNameType

  implicit final val decoder: JsonDecoder[PhoneticNameType] =
    DeriveJsonDecoderEnum.gen[PhoneticNameType]
  implicit final val encoder: JsonEncoder[PhoneticNameType] =
    DeriveJsonEncoderEnum.gen[PhoneticNameType]
  implicit final val codec: JsonCodec[PhoneticNameType] =
    JsonCodec(encoder, decoder)

}
