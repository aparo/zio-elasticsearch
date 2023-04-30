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

package zio.elasticsearch.license.get
import java.time._

import zio.elasticsearch.license._
import zio.json._

final case class LicenseInformation(
  @jsonField("expiry_date") expiryDate: Option[LocalDateTime] = None,
  @jsonField("expiry_date_in_millis") expiryDateInMillis: Option[Long] = None,
  @jsonField("issue_date") issueDate: LocalDateTime,
  @jsonField("issue_date_in_millis") issueDateInMillis: Long,
  @jsonField("issued_to") issuedTo: String,
  issuer: String,
  @jsonField("max_nodes") maxNodes: Option[Long] = None,
  @jsonField("max_resource_units") maxResourceUnits: Option[Int] = None,
  status: LicenseStatus,
  @jsonField("type") `type`: LicenseType,
  uid: String,
  @jsonField("start_date_in_millis") startDateInMillis: Long
)

object LicenseInformation {
  implicit lazy val jsonCodec: JsonCodec[LicenseInformation] =
    DeriveJsonCodec.gen[LicenseInformation]
}
