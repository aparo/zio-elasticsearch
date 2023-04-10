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

package zio.elasticsearch.cluster
import zio._
import zio.elasticsearch.indices.IndexSettings
import zio.elasticsearch.indices.rollover.RolloverConditions
import zio.elasticsearch.mappings.TypeMapping
import zio.json._
import zio.json.ast._
final case class ClusterStateBlockIndex(
  description: Option[String] = None,
  retryable: Option[Boolean] = None,
  levels: Option[Chunk[String]] = None,
  aliases: Option[Chunk[String]] = None,
  @jsonField("aliases_version") aliasesVersion: Option[Int] = None,
  version: Option[Int] = None,
  @jsonField("mapping_version") mappingVersion: Option[Int] = None,
  @jsonField("settings_version") settingsVersion: Option[Int] = None,
  @jsonField("routing_num_shards") routingNumShards: Option[Int] = None,
  state: Option[String] = None,
  settings: Option[Map[String, IndexSettings]] = None,
  @jsonField("in_sync_allocations") inSyncAllocations: Option[
    Map[String, Chunk[String]]
  ] = None,
  @jsonField("primary_terms") primaryTerms: Option[Map[String, Int]] = None,
  mappings: Option[Map[String, TypeMapping]] = None,
  @jsonField("rollover_info") rolloverInfo: Option[
    Map[String, RolloverConditions]
  ] = None,
  @jsonField("timestamp_range") timestampRange: Option[Map[String, Json]] = None,
  system: Option[Boolean] = None
)

object ClusterStateBlockIndex {
  implicit lazy val jsonCodec: JsonCodec[ClusterStateBlockIndex] =
    DeriveJsonCodec.gen[ClusterStateBlockIndex]
}
