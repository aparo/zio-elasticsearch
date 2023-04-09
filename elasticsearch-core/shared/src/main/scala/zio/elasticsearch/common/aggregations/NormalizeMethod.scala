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

package zio.elasticsearch.common.aggregations

import zio.json._

sealed trait NormalizeMethod

object NormalizeMethod {

  case object rescale_0_1 extends NormalizeMethod

  case object rescale_0_100 extends NormalizeMethod

  case object percent_of_sum extends NormalizeMethod

  case object mean extends NormalizeMethod

  case object `z-score` extends NormalizeMethod

  case object softmax extends NormalizeMethod

  implicit final val decoder: JsonDecoder[NormalizeMethod] =
    DeriveJsonDecoderEnum.gen[NormalizeMethod]
  implicit final val encoder: JsonEncoder[NormalizeMethod] =
    DeriveJsonEncoderEnum.gen[NormalizeMethod]
  implicit final val codec: JsonCodec[NormalizeMethod] =
    JsonCodec(encoder, decoder)

}
