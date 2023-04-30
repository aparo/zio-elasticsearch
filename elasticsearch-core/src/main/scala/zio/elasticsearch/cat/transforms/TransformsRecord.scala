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

package zio.elasticsearch.cat.transforms
import zio.json._
final case class TransformsRecord(
  id: Option[String] = None,
  state: Option[String] = None,
  checkpoint: Option[String] = None,
  @jsonField("documents_processed") documentsProcessed: Option[String] = None,
  @jsonField("checkpoint_progress") checkpointProgress: Option[String] = None,
  @jsonField("last_search_time") lastSearchTime: Option[String] = None,
  @jsonField("changes_last_detection_time") changesLastDetectionTime: Option[
    String
  ] = None,
  @jsonField("create_time") createTime: Option[String] = None,
  version: Option[String] = None,
  @jsonField("source_index") sourceIndex: Option[String] = None,
  @jsonField("dest_index") destIndex: Option[String] = None,
  pipeline: Option[String] = None,
  description: Option[String] = None,
  @jsonField("transform_type") transformType: Option[String] = None,
  frequency: Option[String] = None,
  @jsonField("max_page_search_size") maxPageSearchSize: Option[String] = None,
  @jsonField("docs_per_second") docsPerSecond: Option[String] = None,
  reason: Option[String] = None,
  @jsonField("search_total") searchTotal: Option[String] = None,
  @jsonField("search_failure") searchFailure: Option[String] = None,
  @jsonField("search_time") searchTime: Option[String] = None,
  @jsonField("index_total") indexTotal: Option[String] = None,
  @jsonField("index_failure") indexFailure: Option[String] = None,
  @jsonField("index_time") indexTime: Option[String] = None,
  @jsonField("documents_indexed") documentsIndexed: Option[String] = None,
  @jsonField("delete_time") deleteTime: Option[String] = None,
  @jsonField("documents_deleted") documentsDeleted: Option[String] = None,
  @jsonField("trigger_count") triggerCount: Option[String] = None,
  @jsonField("pages_processed") pagesProcessed: Option[String] = None,
  @jsonField("processing_time") processingTime: Option[String] = None,
  @jsonField(
    "checkpoint_duration_time_exp_avg"
  ) checkpointDurationTimeExpAvg: Option[String] = None,
  @jsonField("indexed_documents_exp_avg") indexedDocumentsExpAvg: Option[
    String
  ] = None,
  @jsonField("processed_documents_exp_avg") processedDocumentsExpAvg: Option[
    String
  ] = None
)

object TransformsRecord {
  implicit lazy val jsonCodec: JsonCodec[TransformsRecord] =
    DeriveJsonCodec.gen[TransformsRecord]
}
