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

package zio.elasticsearch.cluster.stats
import zio.json._
import zio.json.ast._
final case class IndexingPressureMemorySummary(
  @jsonField("all_in_bytes") allInBytes: Long,
  @jsonField(
    "combined_coordinating_and_primary_in_bytes"
  ) combinedCoordinatingAndPrimaryInBytes: Long,
  @jsonField("coordinating_in_bytes") coordinatingInBytes: Long,
  @jsonField("coordinating_rejections") coordinatingRejections: Option[Long] = None,
  @jsonField("primary_in_bytes") primaryInBytes: Long,
  @jsonField("primary_rejections") primaryRejections: Option[Long] = None,
  @jsonField("replica_in_bytes") replicaInBytes: Long,
  @jsonField("replica_rejections") replicaRejections: Option[Long] = None
)

object IndexingPressureMemorySummary {
  implicit lazy val jsonCodec: JsonCodec[IndexingPressureMemorySummary] =
    DeriveJsonCodec.gen[IndexingPressureMemorySummary]
}
