/*
 * Copyright 2019 Alberto Paro
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

package zio.elasticsearch.mappings

import zio.json._

@jsonEnumLowerCase
sealed trait NumberMappingType extends EnumLowerCase

object NumberMappingType {
  implicit final val decoder: JsonDecoder[NumberMappingType] =
    DeriveJsonDecoderEnum.gen[NumberMappingType]
  implicit final val encoder: JsonEncoder[NumberMappingType] =
    DeriveJsonEncoderEnum.gen[NumberMappingType]
  implicit final val codec: JsonCodec[NumberMappingType] = JsonCodec(encoder, decoder)
  case object BYTE extends NumberMappingType
  case object DOUBLE extends NumberMappingType
  case object FLOAT extends NumberMappingType
  case object SCALED_FLOAT extends NumberMappingType
  case object HALF_FLOAT extends NumberMappingType
  case object SHORT extends NumberMappingType
  case object INTEGER extends NumberMappingType
  case object LONG extends NumberMappingType

  def withNameInsensitiveOption(str: String): Option[NumberMappingType] = {
    val res = ("\"" + str + "\"").toLowerCase.fromJson[NumberMappingType]
    res.toOption
  }
}
