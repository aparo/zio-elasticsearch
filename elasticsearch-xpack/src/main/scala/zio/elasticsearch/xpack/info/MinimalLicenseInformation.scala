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

package zio.elasticsearch.xpack.info
import zio.elasticsearch.license.{ LicenseStatus, LicenseType }
import zio.json._
final case class MinimalLicenseInformation(
  @jsonField("expiry_date_in_millis") expiryDateInMillis: Long,
  mode: LicenseType,
  status: LicenseStatus,
  @jsonField("type") `type`: LicenseType,
  uid: String
)

object MinimalLicenseInformation {
  implicit lazy val jsonCodec: JsonCodec[MinimalLicenseInformation] =
    DeriveJsonCodec.gen[MinimalLicenseInformation]
}
