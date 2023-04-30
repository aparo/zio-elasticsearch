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

package zio.elasticsearch.sort

import zio.json._

@jsonEnumLowerCase
sealed trait SortOrder extends EnumLowerCase

object SortOrder {
  implicit final val decoder: JsonDecoder[SortOrder] =
    DeriveJsonDecoderEnum.gen[SortOrder]
  implicit final val encoder: JsonEncoder[SortOrder] =
    DeriveJsonEncoderEnum.gen[SortOrder]
  implicit final val codec: JsonCodec[SortOrder] = JsonCodec(encoder, decoder)

  case object Asc extends SortOrder

  case object Desc extends SortOrder

  def apply(value: Boolean): SortOrder =
    if (value) SortOrder.Asc else SortOrder.Desc
}