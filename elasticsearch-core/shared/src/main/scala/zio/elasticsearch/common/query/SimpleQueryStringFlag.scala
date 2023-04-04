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

sealed trait SimpleQueryStringFlag

object SimpleQueryStringFlag {

  case object NONE extends SimpleQueryStringFlag

  case object AND extends SimpleQueryStringFlag

  case object OR extends SimpleQueryStringFlag

  case object NOT extends SimpleQueryStringFlag

  case object PREFIX extends SimpleQueryStringFlag

  case object PHRASE extends SimpleQueryStringFlag

  case object PRECEDENCE extends SimpleQueryStringFlag

  case object ESCAPE extends SimpleQueryStringFlag

  case object WHITESPACE extends SimpleQueryStringFlag

  case object FUZZY extends SimpleQueryStringFlag

  case object NEAR extends SimpleQueryStringFlag

  case object SLOP extends SimpleQueryStringFlag

  case object ALL extends SimpleQueryStringFlag

  implicit final val decoder: JsonDecoder[SimpleQueryStringFlag] =
    DeriveJsonDecoderEnum.gen[SimpleQueryStringFlag]
  implicit final val encoder: JsonEncoder[SimpleQueryStringFlag] =
    DeriveJsonEncoderEnum.gen[SimpleQueryStringFlag]
  implicit final val codec: JsonCodec[SimpleQueryStringFlag] =
    JsonCodec(encoder, decoder)

}
