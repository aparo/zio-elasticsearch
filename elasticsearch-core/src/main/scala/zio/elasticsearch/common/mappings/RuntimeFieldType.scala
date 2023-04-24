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

package zio.elasticsearch.common.mappings

import zio.json._

sealed trait RuntimeFieldType

object RuntimeFieldType {

  case object boolean extends RuntimeFieldType

  case object date extends RuntimeFieldType

  case object double extends RuntimeFieldType

  case object geo_point extends RuntimeFieldType

  case object ip extends RuntimeFieldType

  case object keyword extends RuntimeFieldType

  case object long extends RuntimeFieldType

  case object lookup extends RuntimeFieldType

  implicit final val decoder: JsonDecoder[RuntimeFieldType] =
    DeriveJsonDecoderEnum.gen[RuntimeFieldType]
  implicit final val encoder: JsonEncoder[RuntimeFieldType] =
    DeriveJsonEncoderEnum.gen[RuntimeFieldType]
  implicit final val codec: JsonCodec[RuntimeFieldType] =
    JsonCodec(encoder, decoder)

}
