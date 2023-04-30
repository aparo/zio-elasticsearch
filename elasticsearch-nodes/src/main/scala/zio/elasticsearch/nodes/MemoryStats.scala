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

package zio.elasticsearch.nodes
import zio.json._
final case class MemoryStats(
  @jsonField("adjusted_total_in_bytes") adjustedTotalInBytes: Option[Long] = None,
  resident: Option[String] = None,
  @jsonField("resident_in_bytes") residentInBytes: Option[Long] = None,
  share: Option[String] = None,
  @jsonField("share_in_bytes") shareInBytes: Option[Long] = None,
  @jsonField("total_virtual") totalVirtual: Option[String] = None,
  @jsonField("total_virtual_in_bytes") totalVirtualInBytes: Option[Long] = None,
  @jsonField("total_in_bytes") totalInBytes: Option[Long] = None,
  @jsonField("free_in_bytes") freeInBytes: Option[Long] = None,
  @jsonField("used_in_bytes") usedInBytes: Option[Long] = None
)

object MemoryStats {
  implicit lazy val jsonCodec: JsonCodec[MemoryStats] =
    DeriveJsonCodec.gen[MemoryStats]
}
