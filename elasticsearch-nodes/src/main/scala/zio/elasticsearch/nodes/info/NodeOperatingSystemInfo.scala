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
import zio.json._
final case class NodeOperatingSystemInfo(
  arch: String,
  @jsonField("available_processors") availableProcessors: Int,
  @jsonField("allocated_processors") allocatedProcessors: Option[Int] = None,
  name: String,
  @jsonField("pretty_name") prettyName: String,
  @jsonField("refresh_interval_in_millis") refreshIntervalInMillis: Long,
  version: String,
  cpu: Option[NodeInfoOSCPU] = None,
  mem: Option[NodeInfoMemory] = None,
  swap: Option[NodeInfoMemory] = None
)

object NodeOperatingSystemInfo {
  implicit lazy val jsonCodec: JsonCodec[NodeOperatingSystemInfo] =
    DeriveJsonCodec.gen[NodeOperatingSystemInfo]
}
