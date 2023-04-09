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

sealed trait Refresh

object Refresh {

  case object oolea extends Refresh

  case object `true` extends Refresh

  case object `false` extends Refresh

  case object wait_for extends Refresh

  implicit final val decoder: JsonDecoder[Refresh] =
    DeriveJsonDecoderEnum.gen[Refresh]
  implicit final val encoder: JsonEncoder[Refresh] =
    DeriveJsonEncoderEnum.gen[Refresh]
  implicit final val codec: JsonCodec[Refresh] = JsonCodec(encoder, decoder)

  def fromValue(value: Boolean): Refresh =
    if (value) Refresh.`true` else Refresh.`false`

}
