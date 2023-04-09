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

package zio.elasticsearch.ingest

import zio.json._

sealed trait ConvertType

object ConvertType {

  case object integer extends ConvertType

  case object long extends ConvertType

  case object float extends ConvertType

  case object double extends ConvertType

  case object string extends ConvertType

  case object boolean extends ConvertType

  case object auto extends ConvertType

  implicit final val decoder: JsonDecoder[ConvertType] =
    DeriveJsonDecoderEnum.gen[ConvertType]
  implicit final val encoder: JsonEncoder[ConvertType] =
    DeriveJsonEncoderEnum.gen[ConvertType]
  implicit final val codec: JsonCodec[ConvertType] = JsonCodec(encoder, decoder)

}
