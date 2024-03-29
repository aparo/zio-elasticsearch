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

sealed trait LicenseStatus

object LicenseStatus {

  case object active extends LicenseStatus

  case object valid extends LicenseStatus

  case object invalid extends LicenseStatus

  case object expired extends LicenseStatus

  implicit final val decoder: JsonDecoder[LicenseStatus] =
    DeriveJsonDecoderEnum.gen[LicenseStatus]
  implicit final val encoder: JsonEncoder[LicenseStatus] =
    DeriveJsonEncoderEnum.gen[LicenseStatus]
  implicit final val codec: JsonCodec[LicenseStatus] =
    JsonCodec(encoder, decoder)

}
