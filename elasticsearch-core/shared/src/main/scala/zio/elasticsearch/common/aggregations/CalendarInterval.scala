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

sealed trait CalendarInterval

object CalendarInterval {

  case object second extends CalendarInterval

  case object `1s` extends CalendarInterval

  case object minute extends CalendarInterval

  case object `1m` extends CalendarInterval

  case object hour extends CalendarInterval

  case object `1h` extends CalendarInterval

  case object day extends CalendarInterval

  case object `1d` extends CalendarInterval

  case object week extends CalendarInterval

  case object `1w` extends CalendarInterval

  case object month extends CalendarInterval

  case object `1M` extends CalendarInterval

  case object quarter extends CalendarInterval

  case object `1q` extends CalendarInterval

  case object year extends CalendarInterval

  case object `1Y` extends CalendarInterval

  implicit final val decoder: JsonDecoder[CalendarInterval] =
    DeriveJsonDecoderEnum.gen[CalendarInterval]
  implicit final val encoder: JsonEncoder[CalendarInterval] =
    DeriveJsonEncoderEnum.gen[CalendarInterval]
  implicit final val codec: JsonCodec[CalendarInterval] =
    JsonCodec(encoder, decoder)

}
