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
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.indices.stats.ShardStats
import zio.json._
import zio.json.ast._
final case class Stats(
  @jsonField("adaptive_selection") adaptiveSelection: Option[
    Map[String, AdaptiveSelection]
  ] = None,
  breakers: Option[Map[String, Breaker]] = None,
  fs: Option[FileSystem] = None,
  host: Option[String] = None,
  http: Option[Http] = None,
  ingest: Option[Ingest] = None,
  ip: Option[Chunk[String]] = None,
  jvm: Option[Jvm] = None,
  name: Option[String] = None,
  os: Option[OperatingSystem] = None,
  process: Option[Process] = None,
  roles: Option[NodeRoles] = None,
  script: Option[Scripting] = None,
  @jsonField("script_cache") scriptCache: Option[
    Map[String, Chunk[ScriptCache]]
  ] = None,
  @jsonField("thread_pool") threadPool: Option[Map[String, ThreadCount]] = None,
  timestamp: Option[Long] = None,
  transport: Option[Transport] = None,
  @jsonField("transport_address") transportAddress: Option[TransportAddress] = None,
  attributes: Option[Map[String, String]] = None,
  discovery: Option[Discovery] = None,
  @jsonField("indexing_pressure") indexingPressure: Option[IndexingPressure] = None,
  indices: Option[ShardStats] = None
)

object Stats {
  implicit lazy val jsonCodec: JsonCodec[Stats] = DeriveJsonCodec.gen[Stats]
}
