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

sealed trait Month

object Month {

  case object january extends Month

  case object february extends Month

  case object march extends Month

  case object april extends Month

  case object may extends Month

  case object june extends Month

  case object july extends Month

  case object august extends Month

  case object september extends Month

  case object october extends Month

  case object november extends Month

  case object december extends Month

  implicit final val decoder: JsonDecoder[Month] =
    DeriveJsonDecoderEnum.gen[Month]
  implicit final val encoder: JsonEncoder[Month] =
    DeriveJsonEncoderEnum.gen[Month]
  implicit final val codec: JsonCodec[Month] = JsonCodec(encoder, decoder)

}
