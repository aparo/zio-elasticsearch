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

package zio.elasticsearch.rollup

import zio.json._

sealed trait Metric

object Metric {

  case object min extends Metric

  case object max extends Metric

  case object sum extends Metric

  case object avg extends Metric

  case object value_count extends Metric

  implicit final val decoder: JsonDecoder[Metric] =
    DeriveJsonDecoderEnum.gen[Metric]
  implicit final val encoder: JsonEncoder[Metric] =
    DeriveJsonEncoderEnum.gen[Metric]
  implicit final val codec: JsonCodec[Metric] = JsonCodec(encoder, decoder)

}
