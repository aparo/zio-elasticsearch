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

package zio.elasticsearch.license

import zio.json._

sealed trait LicenseType

object LicenseType {

  case object missing extends LicenseType

  case object trial extends LicenseType

  case object basic extends LicenseType

  case object standard extends LicenseType

  case object dev extends LicenseType

  case object silver extends LicenseType

  case object gold extends LicenseType

  case object platinum extends LicenseType

  case object enterprise extends LicenseType

  implicit final val decoder: JsonDecoder[LicenseType] =
    DeriveJsonDecoderEnum.gen[LicenseType]
  implicit final val encoder: JsonEncoder[LicenseType] =
    DeriveJsonEncoderEnum.gen[LicenseType]
  implicit final val codec: JsonCodec[LicenseType] = JsonCodec(encoder, decoder)

}
