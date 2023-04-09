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

package zio.elasticsearch.responses.indices

import zio.json._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-stats.html
 *
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param metric Limit the information returned the specific metrics.
 * @param fielddataFields A comma-separated list of fields for `fielddata` index metric (supports wildcards)
 * @param groups A comma-separated list of search groups for `search` index metric
 * @param completionFields A comma-separated list of fields for `fielddata` and `suggest` index metric (supports wildcards)
 * @param includeSegmentFileSizes Whether to report the aggregated disk usage of each one of the Lucene index files (only applies if segment stats are requested)
 * @param fields A comma-separated list of fields for `fielddata` and `completion` index metric (supports wildcards)
 * @param types A comma-separated list of document types for the `indexing` index metric
 * @param level Return stats aggregated at cluster, index or shard level
 */
final case class IndicesStatsResponse(_ok: Option[Boolean] = None)
object IndicesStatsResponse {
  implicit val jsonDecoder: JsonDecoder[IndicesStatsResponse] = DeriveJsonDecoder.gen[IndicesStatsResponse]
  implicit val jsonEncoder: JsonEncoder[IndicesStatsResponse] = DeriveJsonEncoder.gen[IndicesStatsResponse]
}
