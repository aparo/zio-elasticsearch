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

package zio.elasticsearch.indices.stats
import zio.json._
import zio.json.ast._
final case class ShardFileSizeInfo(
  description: String,
  @jsonField("size_in_bytes") sizeInBytes: Long,
  @jsonField("min_size_in_bytes") minSizeInBytes: Option[Long] = None,
  @jsonField("max_size_in_bytes") maxSizeInBytes: Option[Long] = None,
  @jsonField("average_size_in_bytes") averageSizeInBytes: Option[Long] = None,
  count: Option[Long] = None
)

object ShardFileSizeInfo {
  implicit lazy val jsonCodec: JsonCodec[ShardFileSizeInfo] =
    DeriveJsonCodec.gen[ShardFileSizeInfo]
}
