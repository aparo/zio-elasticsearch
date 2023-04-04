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

package zio.elasticsearch.watcher

import zio.json._

sealed trait Day

object Day {

  case object sunday extends Day

  case object monday extends Day

  case object tuesday extends Day

  case object wednesday extends Day

  case object thursday extends Day

  case object friday extends Day

  case object saturday extends Day

  implicit final val decoder: JsonDecoder[Day] = DeriveJsonDecoderEnum.gen[Day]
  implicit final val encoder: JsonEncoder[Day] = DeriveJsonEncoderEnum.gen[Day]
  implicit final val codec: JsonCodec[Day] = JsonCodec(encoder, decoder)

}
