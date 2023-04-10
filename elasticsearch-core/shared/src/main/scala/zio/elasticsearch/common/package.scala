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

  type CronExpression = String

  type DataStreamName = String

  type ScrollId = String

  type Username = String
  type Password = String

  type AggregateName = String
  type SuggestionName = String
  type TrackHits = Boolean

  type Aggregate = zio.elasticsearch.responses.aggregations.Aggregation
  type DataStreamNames = Chunk[String]

  type PipelineName = String
  type Routing = String

  type TransportAddress = String
  type NodeName = String
  type NodeId = String

  type NodeIds = Chunk[NodeId] // | NodeId[]

  type NodeRoles = Chunk[NodeRole]
  type ScrollIds = Chunk[String]

  type Time = String

  type DateFormat = String

  type DateMath = String

  type DateTime = String //string | EpochTime<UnitMillis>

  type Distance = String

  type Indices = Chunk[String]
  type Names = Chunk[String]
  type Metadata = Map[String, String]

  type WaitForActiveShards = Json //integer | WaitForActiveShardOptions
  type Percentage = Json //string | float
  type Duration = Json //string | -1 | 0

  type DurationLarge = String

  type RuntimeFields = Json
  type TimeZone = Json
  type FieldAndFormat = Json

  type TPartialDocument = Json
  type TDocument = Json

  type QueryVector = Chunk[Double]

  type MinimumShouldMatch = Int

  type GeoHashPrecision = Json

  type SortResults = Chunk[Json]

  type FieldType = String //'none' | 'geo_point' | 'geo_shape' | 'ip' | 'binary' | 'keyword' | 'text' | 'search_as_you_type' | 'date' | 'date_nanos' | 'boolean' | 'completion' | 'nested' | 'object' | 'murmur3' | 'token_count' | 'percolator' | 'integer' | 'long' | 'short' | 'byte' | 'float' | 'half_float' | 'scaled_float' | 'double' | 'integer_range' | 'float_range' | 'long_range' | 'double_range' | 'date_range' | 'ip_range' | 'alias' | 'join' | 'rank_feature' | 'rank_features' | 'flattened' | 'shape' | 'histogram' | 'constant_keyword' | 'aggregate_metric_double' | 'dense_vector' | 'match_only_text'

  type MinimumInterval = String // 'second' | 'minute' | 'hour' | 'day' | 'month' | 'year'

  type Fuzziness = Json

  type Missing = Json //string | integer | double | boolean

  type GridAggregationType = String //'geotile' | 'geohex'

  type IntervalUnit = Json

}
