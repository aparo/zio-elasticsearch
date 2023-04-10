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

sealed trait ActionType

object ActionType {

  case object email extends ActionType

  case object webhook extends ActionType

  case object index extends ActionType

  case object logging extends ActionType

  case object slack extends ActionType

  case object pagerduty extends ActionType

  implicit final val decoder: JsonDecoder[ActionType] =
    DeriveJsonDecoderEnum.gen[ActionType]
  implicit final val encoder: JsonEncoder[ActionType] =
    DeriveJsonEncoderEnum.gen[ActionType]
  implicit final val codec: JsonCodec[ActionType] = JsonCodec(encoder, decoder)

}