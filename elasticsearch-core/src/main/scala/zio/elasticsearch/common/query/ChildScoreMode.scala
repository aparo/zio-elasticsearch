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

package zio.elasticsearch.common.query

import zio.json._

sealed trait ChildScoreMode

object ChildScoreMode {

  case object none extends ChildScoreMode

  case object avg extends ChildScoreMode

  case object sum extends ChildScoreMode

  case object max extends ChildScoreMode

  case object min extends ChildScoreMode

  implicit final val decoder: JsonDecoder[ChildScoreMode] =
    DeriveJsonDecoderEnum.gen[ChildScoreMode]
  implicit final val encoder: JsonEncoder[ChildScoreMode] =
    DeriveJsonEncoderEnum.gen[ChildScoreMode]
  implicit final val codec: JsonCodec[ChildScoreMode] =
    JsonCodec(encoder, decoder)

}
