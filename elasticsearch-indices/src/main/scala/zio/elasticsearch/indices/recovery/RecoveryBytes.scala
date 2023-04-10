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

package zio.elasticsearch.indices.recovery
import zio.elasticsearch.common.Percentage
import zio.json._
import zio.json.ast._
final case class RecoveryBytes(
  percent: Percentage,
  recovered: Option[String] = None,
  @jsonField("recovered_in_bytes") recoveredInBytes: String,
  @jsonField("recovered_from_snapshot") recoveredFromSnapshot: Option[
    String
  ] = None,
  @jsonField(
    "recovered_from_snapshot_in_bytes"
  ) recoveredFromSnapshotInBytes: Option[String] = None,
  reused: Option[String] = None,
  @jsonField("reused_in_bytes") reusedInBytes: String,
  total: Option[String] = None,
  @jsonField("total_in_bytes") totalInBytes: String
)

object RecoveryBytes {
  implicit lazy val jsonCodec: JsonCodec[RecoveryBytes] =
    DeriveJsonCodec.gen[RecoveryBytes]
}
