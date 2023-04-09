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

sealed trait DataframeState

object DataframeState {

  case object started extends DataframeState

  case object stopped extends DataframeState

  case object starting extends DataframeState

  case object stopping extends DataframeState

  case object failed extends DataframeState

  implicit final val decoder: JsonDecoder[DataframeState] =
    DeriveJsonDecoderEnum.gen[DataframeState]
  implicit final val encoder: JsonEncoder[DataframeState] =
    DeriveJsonEncoderEnum.gen[DataframeState]
  implicit final val codec: JsonCodec[DataframeState] =
    JsonCodec(encoder, decoder)

}
