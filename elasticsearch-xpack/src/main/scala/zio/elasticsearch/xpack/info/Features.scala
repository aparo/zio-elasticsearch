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

package zio.elasticsearch.xpack.info
import zio.json._
final case class Features(
  @jsonField("aggregate_metric") aggregateMetric: Feature,
  analytics: Feature,
  ccr: Feature,
  @jsonField("data_frame") dataFrame: Option[Feature] = None,
  @jsonField("data_science") dataScience: Option[Feature] = None,
  @jsonField("data_streams") dataStreams: Feature,
  @jsonField("data_tiers") dataTiers: Feature,
  enrich: Feature,
  eql: Feature,
  flattened: Option[Feature] = None,
  @jsonField("frozen_indices") frozenIndices: Feature,
  graph: Feature,
  ilm: Feature,
  logstash: Feature,
  ml: Feature,
  monitoring: Feature,
  rollup: Feature,
  @jsonField("runtime_fields") runtimeFields: Option[Feature] = None,
  @jsonField("searchable_snapshots") searchableSnapshots: Feature,
  security: Feature,
  slm: Feature,
  spatial: Feature,
  sql: Feature,
  transform: Feature,
  vectors: Option[Feature] = None,
  @jsonField("voting_only") votingOnly: Feature,
  watcher: Feature,
  archive: Feature
)

object Features {
  implicit lazy val jsonCodec: JsonCodec[Features] = DeriveJsonCodec.gen[Features]
}
