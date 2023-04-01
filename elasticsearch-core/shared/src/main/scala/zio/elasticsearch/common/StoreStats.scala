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
import zio.json.ast._
final case class StoreStats(
  size: Option[String] = None,
  @jsonField("size_in_bytes") sizeInBytes: Int,
  reserved: Option[String] = None,
  @jsonField("reserved_in_bytes") reservedInBytes: Int,
  @jsonField("total_data_set_size") totalDataSetSize: Option[String] = None,
  @jsonField("total_data_set_size_in_bytes") totalDataSetSizeInBytes: Option[
    Int
  ] = None
)

object StoreStats {
  implicit val jsonCodec: JsonCodec[StoreStats] =
    DeriveJsonCodec.gen[StoreStats]
}
