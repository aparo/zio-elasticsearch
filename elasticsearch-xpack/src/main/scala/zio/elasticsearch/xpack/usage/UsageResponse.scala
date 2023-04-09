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

package zio.elasticsearch.xpack.usage
import zio.json._
import zio.json.ast._
/*
 * Retrieves usage information about the installed X-Pack features.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/usage-api.html
 *
 * @param aggregateMetric

 * @param analytics

 * @param archive
@since 8.2.0

 * @param watcher

 * @param ccr

 * @param dataFrame

 * @param dataScience

 * @param dataStreams

 * @param dataTiers

 * @param enrich

 * @param eql

 * @param flattened

 * @param frozenIndices

 * @param graph

 * @param healthApi

 * @param ilm

 * @param logstash

 * @param ml

 * @param monitoring

 * @param rollup

 * @param runtimeFields

 * @param spatial

 * @param searchableSnapshots

 * @param security

 * @param slm

 * @param sql

 * @param transform

 * @param vectors

 * @param votingOnly

 */
final case class UsageResponse(
  aggregateMetric: Base,
  analytics: Analytics,
  archive: Archive,
  watcher: Watcher,
  ccr: Ccr,
  dataFrame: Base,
  dataScience: Base,
  dataStreams: DataStreams,
  dataTiers: DataTiers,
  enrich: Base,
  eql: Eql,
  flattened: Flattened,
  frozenIndices: FrozenIndices,
  graph: Base,
  healthApi: HealthStatistics,
  ilm: Ilm,
  logstash: Base,
  ml: MachineLearning,
  monitoring: Monitoring,
  rollup: Base,
  runtimeFields: RuntimeFieldTypes,
  spatial: Base,
  searchableSnapshots: SearchableSnapshots,
  security: Security,
  slm: Slm,
  sql: Sql,
  transform: Base,
  vectors: Vector,
  votingOnly: Base
) {}
object UsageResponse {
  implicit val jsonCodec: JsonCodec[UsageResponse] =
    DeriveJsonCodec.gen[UsageResponse]
}
