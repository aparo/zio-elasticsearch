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

package zio.elasticsearch

import zio.Chunk
import zio.json.ast.Json

package object common {
  type DataStreamName = String
  type DataStreamNames = Chunk[String]

  type PipelineName = String
  type QueryContainer = zio.elasticsearch.queries.Query
  type Routing = String

  type TransportAddress = String
  type NodeName = String
  type NodeId = String

  type NodeIds = Chunk[NodeId] // | NodeId[]

  type NodeRole = String //'master' | 'data' | 'data_cold' | 'data_content' | 'data_frozen' | 'data_hot' | 'data_warm' | 'client' | 'ingest' | 'ml' | 'voting_only' | 'transform' | 'remote_cluster_client' | 'coordinating_only'

  type NodeRoles = Chunk[NodeRole]

  type Bytes = String
  type Time = String
  type VersionType = String
  type TimeUnit = String

  type DateFormat = String

  type DateMath = String

  type DateTime = String //string | EpochTime<UnitMillis>

  type Distance = String

  type DistanceUnit = String //'in' | 'ft' | 'yd' | 'mi' | 'nmi' | 'km' | 'm' | 'cm' | 'mm'

  type Indices = Chunk[String]
  type Names = Chunk[String]
  type Metadata = Map[String, String]

  type GeoShapeRelation = String //  'intersects' | 'disjoint' | 'within' | 'contains'
  type HealthStatus = String // 'green' | 'GREEN' | 'yellow' | 'YELLOW' | 'red' | 'RED'
  type WaitForActiveShardOptions = String // 'all' | 'index-setting'

  type WaitForActiveShards = Json //integer | WaitForActiveShardOptions
  type Percentage = Json //string | float
  type Duration = Json //string | -1 | 0

  type DurationLarge = String

}
