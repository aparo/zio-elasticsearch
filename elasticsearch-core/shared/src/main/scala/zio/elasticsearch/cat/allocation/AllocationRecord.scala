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

package zio.elasticsearch.cat.allocation
import zio.elasticsearch.common.Percentage
import zio.json._
import zio.json.ast._
final case class AllocationRecord(
  shards: Option[String] = None,
  @jsonField("disk.indices") `disk.indices`: Option[String] = None,
  @jsonField("disk.used") `disk.used`: Option[String] = None,
  @jsonField("disk.avail") `disk.avail`: Option[String] = None,
  @jsonField("disk.total") `disk.total`: Option[String] = None,
  @jsonField("disk.percent") `disk.percent`: Option[Percentage] = None,
  host: Option[String] = None,
  ip: Option[String] = None,
  node: Option[String] = None
)

object AllocationRecord {
  implicit val jsonCodec: JsonCodec[AllocationRecord] =
    DeriveJsonCodec.gen[AllocationRecord]
}
