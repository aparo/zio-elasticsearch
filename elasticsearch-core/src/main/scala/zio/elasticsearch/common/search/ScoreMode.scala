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

package zio.elasticsearch.common.search

import zio.json._

sealed trait ScoreMode

object ScoreMode {

  case object avg extends ScoreMode

  case object max extends ScoreMode

  case object min extends ScoreMode

  case object multiply extends ScoreMode

  case object total extends ScoreMode

  implicit final val decoder: JsonDecoder[ScoreMode] =
    DeriveJsonDecoderEnum.gen[ScoreMode]
  implicit final val encoder: JsonEncoder[ScoreMode] =
    DeriveJsonEncoderEnum.gen[ScoreMode]
  implicit final val codec: JsonCodec[ScoreMode] = JsonCodec(encoder, decoder)

}
