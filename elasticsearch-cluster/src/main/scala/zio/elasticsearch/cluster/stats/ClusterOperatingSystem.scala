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
import zio._
import zio.json._
final case class ClusterOperatingSystem(
  @jsonField("allocated_processors") allocatedProcessors: Int,
  @jsonField("available_processors") availableProcessors: Int,
  mem: OperatingSystemMemoryInfo,
  names: Chunk[ClusterOperatingSystemName],
  @jsonField("pretty_names") prettyNames: Chunk[
    ClusterOperatingSystemPrettyName
  ],
  architectures: Option[Chunk[ClusterOperatingSystemArchitecture]] = None
)

object ClusterOperatingSystem {
  implicit lazy val jsonCodec: JsonCodec[ClusterOperatingSystem] =
    DeriveJsonCodec.gen[ClusterOperatingSystem]
}
