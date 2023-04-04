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

sealed trait DFRBasicModel

object DFRBasicModel {

  case object be extends DFRBasicModel

  case object d extends DFRBasicModel

  case object g extends DFRBasicModel

  case object `if` extends DFRBasicModel

  case object in extends DFRBasicModel

  case object ine extends DFRBasicModel

  case object p extends DFRBasicModel

  implicit final val decoder: JsonDecoder[DFRBasicModel] = DeriveJsonDecoderEnum.gen[DFRBasicModel]
  implicit final val encoder: JsonEncoder[DFRBasicModel] = DeriveJsonEncoderEnum.gen[DFRBasicModel]
  implicit final val codec: JsonCodec[DFRBasicModel] = JsonCodec(encoder, decoder)

}
