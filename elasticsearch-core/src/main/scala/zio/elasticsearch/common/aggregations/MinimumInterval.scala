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

sealed trait MinimumInterval

object MinimumInterval {

  case object second extends MinimumInterval

  case object minute extends MinimumInterval

  case object hour extends MinimumInterval

  case object day extends MinimumInterval

  case object month extends MinimumInterval

  case object year extends MinimumInterval

  implicit final val decoder: JsonDecoder[MinimumInterval] =
    DeriveJsonDecoderEnum.gen[MinimumInterval]
  implicit final val encoder: JsonEncoder[MinimumInterval] =
    DeriveJsonEncoderEnum.gen[MinimumInterval]
  implicit final val codec: JsonCodec[MinimumInterval] =
    JsonCodec(encoder, decoder)

}
