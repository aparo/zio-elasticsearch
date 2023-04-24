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

package zio.elasticsearch.cat

import zio.json._

sealed trait CatDfaColumn

object CatDfaColumn {

  case object assignment_explanation extends CatDfaColumn

  case object ae extends CatDfaColumn

  case object create_time extends CatDfaColumn

  case object ct extends CatDfaColumn

  case object createTime extends CatDfaColumn

  case object description extends CatDfaColumn

  case object d extends CatDfaColumn

  case object dest_index extends CatDfaColumn

  case object di extends CatDfaColumn

  case object destIndex extends CatDfaColumn

  case object failure_reason extends CatDfaColumn

  case object fr extends CatDfaColumn

  case object failureReason extends CatDfaColumn

  case object id extends CatDfaColumn

  case object model_memory_limit extends CatDfaColumn

  case object mml extends CatDfaColumn

  case object modelMemoryLimit extends CatDfaColumn

  case object `node.address` extends CatDfaColumn

  case object na extends CatDfaColumn

  case object nodeAddress extends CatDfaColumn

  case object `node.ephemeral_id` extends CatDfaColumn

  case object ne extends CatDfaColumn

  case object nodeEphemeralId extends CatDfaColumn

  case object `node.id` extends CatDfaColumn

  case object ni extends CatDfaColumn

  case object nodeId extends CatDfaColumn

  case object `node.name` extends CatDfaColumn

  case object nn extends CatDfaColumn

  case object nodeName extends CatDfaColumn

  case object progress extends CatDfaColumn

  case object p extends CatDfaColumn

  case object source_index extends CatDfaColumn

  case object si extends CatDfaColumn

  case object sourceIndex extends CatDfaColumn

  case object state extends CatDfaColumn

  case object s extends CatDfaColumn

  case object `type` extends CatDfaColumn

  case object t extends CatDfaColumn

  case object version extends CatDfaColumn

  case object v extends CatDfaColumn

  implicit final val decoder: JsonDecoder[CatDfaColumn] =
    DeriveJsonDecoderEnum.gen[CatDfaColumn]
  implicit final val encoder: JsonEncoder[CatDfaColumn] =
    DeriveJsonEncoderEnum.gen[CatDfaColumn]
  implicit final val codec: JsonCodec[CatDfaColumn] =
    JsonCodec(encoder, decoder)

}
