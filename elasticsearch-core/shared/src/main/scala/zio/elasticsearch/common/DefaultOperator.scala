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
sealed trait DefaultOperator extends EnumLowerCase

object DefaultOperator {

  case object AND extends DefaultOperator

  case object OR extends DefaultOperator

  implicit final val decoder: JsonDecoder[DefaultOperator] =
    DeriveJsonDecoderEnum.gen[DefaultOperator]
  implicit final val encoder: JsonEncoder[DefaultOperator] =
    DeriveJsonEncoderEnum.gen[DefaultOperator]
  implicit final val codec: JsonCodec[DefaultOperator] = JsonCodec(encoder, decoder)

}
