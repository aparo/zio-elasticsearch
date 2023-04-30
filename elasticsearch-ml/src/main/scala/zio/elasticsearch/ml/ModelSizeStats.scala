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

package zio.elasticsearch.ml
import java.time._

import zio.json._
final case class ModelSizeStats(
  @jsonField(
    "bucket_allocation_failures_count"
  ) bucketAllocationFailuresCount: Long,
  @jsonField("job_id") jobId: String,
  @jsonField("log_time") logTime: LocalDateTime,
  @jsonField("memory_status") memoryStatus: MemoryStatus,
  @jsonField("model_bytes") modelBytes: String,
  @jsonField("model_bytes_exceeded") modelBytesExceeded: Option[String] = None,
  @jsonField("model_bytes_memory_limit") modelBytesMemoryLimit: Option[
    String
  ] = None,
  @jsonField("peak_model_bytes") peakModelBytes: Option[String] = None,
  @jsonField("assignment_memory_basis") assignmentMemoryBasis: Option[
    String
  ] = None,
  @jsonField("result_type") resultType: String,
  @jsonField("total_by_field_count") totalByFieldCount: Long,
  @jsonField("total_over_field_count") totalOverFieldCount: Long,
  @jsonField("total_partition_field_count") totalPartitionFieldCount: Long,
  @jsonField(
    "categorization_status"
  ) categorizationStatus: CategorizationStatus,
  @jsonField("categorized_doc_count") categorizedDocCount: Int,
  @jsonField("dead_category_count") deadCategoryCount: Int,
  @jsonField("failed_category_count") failedCategoryCount: Int,
  @jsonField("frequent_category_count") frequentCategoryCount: Int,
  @jsonField("rare_category_count") rareCategoryCount: Int,
  @jsonField("total_category_count") totalCategoryCount: Int,
  timestamp: Option[Long] = None
)

object ModelSizeStats {
  implicit lazy val jsonCodec: JsonCodec[ModelSizeStats] =
    DeriveJsonCodec.gen[ModelSizeStats]
}
