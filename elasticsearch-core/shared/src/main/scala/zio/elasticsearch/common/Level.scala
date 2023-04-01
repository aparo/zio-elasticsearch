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

import zio.json.{
  DeriveJsonDecoderEnum,
  DeriveJsonEncoderEnum,
  EnumLowerCase,
  JsonCodec,
  JsonDecoder,
  JsonEncoder,
  jsonEnumLowerCase
}

@jsonEnumLowerCase
sealed trait Level extends EnumLowerCase

case object Level {

  case object cluster extends Level

  case object indices extends Level

  case object node extends Level

  case object shards extends Level

  implicit final val decoder: JsonDecoder[Level] =
    DeriveJsonDecoderEnum.gen[Level]
  implicit final val encoder: JsonEncoder[Level] =
    DeriveJsonEncoderEnum.gen[Level]
  implicit final val codec: JsonCodec[Level] = JsonCodec(encoder, decoder)
}
