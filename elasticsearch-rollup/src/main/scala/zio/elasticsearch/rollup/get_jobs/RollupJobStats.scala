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

package zio.elasticsearch.rollup.get_jobs
import zio.json._
final case class RollupJobStats(
  @jsonField("documents_processed") documentsProcessed: Long,
  @jsonField("index_failures") indexFailures: Long,
  @jsonField("index_time_in_ms") indexTimeInMs: Long,
  @jsonField("index_total") indexTotal: Long,
  @jsonField("pages_processed") pagesProcessed: Long,
  @jsonField("rollups_indexed") rollupsIndexed: Long,
  @jsonField("search_failures") searchFailures: Long,
  @jsonField("search_time_in_ms") searchTimeInMs: Long,
  @jsonField("search_total") searchTotal: Long,
  @jsonField("trigger_count") triggerCount: Long,
  @jsonField("processing_time_in_ms") processingTimeInMs: Long,
  @jsonField("processing_total") processingTotal: Long
)

object RollupJobStats {
  implicit lazy val jsonCodec: JsonCodec[RollupJobStats] =
    DeriveJsonCodec.gen[RollupJobStats]
}
