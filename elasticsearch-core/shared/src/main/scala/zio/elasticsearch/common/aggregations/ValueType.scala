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

package zio.elasticsearch.common.aggregations

import zio.json._

sealed trait ValueType

object ValueType {

  case object string extends ValueType

  case object long extends ValueType

  case object double extends ValueType

  case object number extends ValueType

  case object date extends ValueType

  case object date_nanos extends ValueType

  case object ip extends ValueType

  case object numeric extends ValueType

  case object geo_point extends ValueType

  case object boolean extends ValueType

  implicit final val decoder: JsonDecoder[ValueType] =
    DeriveJsonDecoderEnum.gen[ValueType]
  implicit final val encoder: JsonEncoder[ValueType] =
    DeriveJsonEncoderEnum.gen[ValueType]
  implicit final val codec: JsonCodec[ValueType] = JsonCodec(encoder, decoder)

}
