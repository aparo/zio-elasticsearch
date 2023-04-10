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

package zio.elasticsearch.transform.get_transform_stats
import zio.json._
import zio.json.ast._
final case class TransformIndexerStats(
  @jsonField("delete_time_in_ms") deleteTimeInMs: Option[Long] = None,
  @jsonField("documents_indexed") documentsIndexed: Long,
  @jsonField("documents_deleted") documentsDeleted: Option[Long] = None,
  @jsonField("documents_processed") documentsProcessed: Long,
  @jsonField(
    "exponential_avg_checkpoint_duration_ms"
  ) exponentialAvgCheckpointDurationMs: Double,
  @jsonField(
    "exponential_avg_documents_indexed"
  ) exponentialAvgDocumentsIndexed: Double,
  @jsonField(
    "exponential_avg_documents_processed"
  ) exponentialAvgDocumentsProcessed: Double,
  @jsonField("index_failures") indexFailures: Long,
  @jsonField("index_time_in_ms") indexTimeInMs: Long,
  @jsonField("index_total") indexTotal: Long,
  @jsonField("pages_processed") pagesProcessed: Long,
  @jsonField("processing_time_in_ms") processingTimeInMs: Long,
  @jsonField("processing_total") processingTotal: Long,
  @jsonField("search_failures") searchFailures: Long,
  @jsonField("search_time_in_ms") searchTimeInMs: Long,
  @jsonField("search_total") searchTotal: Long,
  @jsonField("trigger_count") triggerCount: Long
)

object TransformIndexerStats {
  implicit lazy val jsonCodec: JsonCodec[TransformIndexerStats] =
    DeriveJsonCodec.gen[TransformIndexerStats]
}
