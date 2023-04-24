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

package zio.elasticsearch.common.mappings

import zio.json._

sealed trait DynamicMapping

object DynamicMapping {

  case object oolea extends DynamicMapping

  case object strict extends DynamicMapping

  case object runtime extends DynamicMapping

  case object `true` extends DynamicMapping

  case object `false` extends DynamicMapping

  implicit final val decoder: JsonDecoder[DynamicMapping] =
    DeriveJsonDecoderEnum.gen[DynamicMapping]
  implicit final val encoder: JsonEncoder[DynamicMapping] =
    DeriveJsonEncoderEnum.gen[DynamicMapping]
  implicit final val codec: JsonCodec[DynamicMapping] =
    JsonCodec(encoder, decoder)

}
