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

package zio.elasticsearch.common

import zio.json._

sealed trait Normalization

object Normalization {

  case object no extends Normalization

  case object h1 extends Normalization

  case object h2 extends Normalization

  case object h3 extends Normalization

  case object z extends Normalization

  implicit final val decoder: JsonDecoder[Normalization] =
    DeriveJsonDecoderEnum.gen[Normalization]
  implicit final val encoder: JsonEncoder[Normalization] =
    DeriveJsonEncoderEnum.gen[Normalization]
  implicit final val codec: JsonCodec[Normalization] =
    JsonCodec(encoder, decoder)

}
