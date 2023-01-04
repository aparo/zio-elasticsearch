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
sealed trait NumberType extends EnumLowerCase {
  import NumberType._
  def entryName: String = this.toJson.replace("\"", "")
}

object NumberType {
  implicit final val decoder: JsonDecoder[NumberType] =
    DeriveJsonDecoderEnum.gen[NumberType]
  implicit final val encoder: JsonEncoder[NumberType] =
    DeriveJsonEncoderEnum.gen[NumberType]
  implicit final val codec: JsonCodec[NumberType] = JsonCodec(encoder, decoder)
  case object HALF_FLOAT extends NumberType

  case object FLOAT extends NumberType

  case object DOUBLE extends NumberType

  case object BYTE extends NumberType

  case object SHORT extends NumberType

  case object INTEGER extends NumberType

  case object LONG extends NumberType

}
