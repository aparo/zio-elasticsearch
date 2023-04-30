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

package zio.elasticsearch.common
import zio.json._
final case class BulkStats(
  @jsonField("total_operations") totalOperations: Long,
  @jsonField("total_time") totalTime: Option[String] = None,
  @jsonField("total_time_in_millis") totalTimeInMillis: Long,
  @jsonField("total_size") totalSize: Option[String] = None,
  @jsonField("total_size_in_bytes") totalSizeInBytes: Long,
  @jsonField("avg_time") avgTime: Option[String] = None,
  @jsonField("avg_time_in_millis") avgTimeInMillis: Long,
  @jsonField("avg_size") avgSize: Option[String] = None,
  @jsonField("avg_size_in_bytes") avgSizeInBytes: Long
)

object BulkStats {
  implicit lazy val jsonCodec: JsonCodec[BulkStats] = DeriveJsonCodec.gen[BulkStats]
}
