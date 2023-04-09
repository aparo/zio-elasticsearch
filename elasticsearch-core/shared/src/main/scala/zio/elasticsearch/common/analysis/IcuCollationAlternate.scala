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

sealed trait IcuCollationAlternate

object IcuCollationAlternate {

  case object shifted extends IcuCollationAlternate

  case object `non-ignorable` extends IcuCollationAlternate

  implicit final val decoder: JsonDecoder[IcuCollationAlternate] =
    DeriveJsonDecoderEnum.gen[IcuCollationAlternate]
  implicit final val encoder: JsonEncoder[IcuCollationAlternate] =
    DeriveJsonEncoderEnum.gen[IcuCollationAlternate]
  implicit final val codec: JsonCodec[IcuCollationAlternate] =
    JsonCodec(encoder, decoder)

}
