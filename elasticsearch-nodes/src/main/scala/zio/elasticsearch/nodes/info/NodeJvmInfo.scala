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

package zio.elasticsearch.nodes.info
import zio._
import zio.json._
import zio.json.ast._
final case class NodeJvmInfo(
  @jsonField("gc_collectors") gcCollectors: Chunk[String],
  mem: NodeInfoJvmMemory,
  @jsonField("memory_pools") memoryPools: Chunk[String],
  pid: Int,
  @jsonField("start_time_in_millis") startTimeInMillis: Long,
  version: String,
  @jsonField("vm_name") vmName: String,
  @jsonField("vm_vendor") vmVendor: String,
  @jsonField("vm_version") vmVersion: String,
  @jsonField("using_bundled_jdk") usingBundledJdk: Boolean,
  @jsonField(
    "using_compressed_ordinary_object_pointers"
  ) usingCompressedOrdinaryObjectPointers: Option[Json] = None,
  @jsonField("input_arguments") inputArguments: Chunk[String]
)

object NodeJvmInfo {
  implicit lazy val jsonCodec: JsonCodec[NodeJvmInfo] =
    DeriveJsonCodec.gen[NodeJvmInfo]
}
