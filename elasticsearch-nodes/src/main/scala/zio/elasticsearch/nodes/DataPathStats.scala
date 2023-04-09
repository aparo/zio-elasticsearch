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
import zio.json.ast._
final case class DataPathStats(
  available: Option[String] = None,
  @jsonField("available_in_bytes") availableInBytes: Option[Long] = None,
  @jsonField("disk_queue") diskQueue: Option[String] = None,
  @jsonField("disk_reads") diskReads: Option[Long] = None,
  @jsonField("disk_read_size") diskReadSize: Option[String] = None,
  @jsonField("disk_read_size_in_bytes") diskReadSizeInBytes: Option[Long] = None,
  @jsonField("disk_writes") diskWrites: Option[Long] = None,
  @jsonField("disk_write_size") diskWriteSize: Option[String] = None,
  @jsonField("disk_write_size_in_bytes") diskWriteSizeInBytes: Option[Long] = None,
  free: Option[String] = None,
  @jsonField("free_in_bytes") freeInBytes: Option[Long] = None,
  mount: Option[String] = None,
  path: Option[String] = None,
  total: Option[String] = None,
  @jsonField("total_in_bytes") totalInBytes: Option[Long] = None,
  @jsonField("type") `type`: Option[String] = None
)

object DataPathStats {
  implicit val jsonCodec: JsonCodec[DataPathStats] =
    DeriveJsonCodec.gen[DataPathStats]
}
