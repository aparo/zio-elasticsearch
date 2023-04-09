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

package zio.elasticsearch.ml

import zio.json._

sealed trait RoutingState

object RoutingState {

  case object failed extends RoutingState

  case object started extends RoutingState

  case object starting extends RoutingState

  case object stopped extends RoutingState

  case object stopping extends RoutingState

  implicit final val decoder: JsonDecoder[RoutingState] =
    DeriveJsonDecoderEnum.gen[RoutingState]
  implicit final val encoder: JsonEncoder[RoutingState] =
    DeriveJsonEncoderEnum.gen[RoutingState]
  implicit final val codec: JsonCodec[RoutingState] =
    JsonCodec(encoder, decoder)

}
