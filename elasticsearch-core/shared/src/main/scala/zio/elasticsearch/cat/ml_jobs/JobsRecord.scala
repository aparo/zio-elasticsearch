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

package zio.elasticsearch.cat.ml_jobs
import zio.elasticsearch.ml.{ CategorizationStatus, JobState, MemoryStatus }
import zio.json._
final case class JobsRecord(
  id: Option[String] = None,
  state: Option[JobState] = None,
  @jsonField("opened_time") openedTime: Option[String] = None,
  @jsonField("assignment_explanation") assignmentExplanation: Option[String] = None,
  @jsonField("data.processed_records") `data.processedRecords`: Option[
    String
  ] = None,
  @jsonField("data.processed_fields") `data.processedFields`: Option[String] = None,
  @jsonField("data.input_bytes") `data.inputBytes`: Option[String] = None,
  @jsonField("data.input_records") `data.inputRecords`: Option[String] = None,
  @jsonField("data.input_fields") `data.inputFields`: Option[String] = None,
  @jsonField("data.invalid_dates") `data.invalidDates`: Option[String] = None,
  @jsonField("data.missing_fields") `data.missingFields`: Option[String] = None,
  @jsonField(
    "data.out_of_order_timestamps"
  ) `data.outOfOrderTimestamps`: Option[String] = None,
  @jsonField("data.empty_buckets") `data.emptyBuckets`: Option[String] = None,
  @jsonField("data.sparse_buckets") `data.sparseBuckets`: Option[String] = None,
  @jsonField("data.buckets") `data.buckets`: Option[String] = None,
  @jsonField("data.earliest_record") `data.earliestRecord`: Option[String] = None,
  @jsonField("data.latest_record") `data.latestRecord`: Option[String] = None,
  @jsonField("data.last") `data.last`: Option[String] = None,
  @jsonField("data.last_empty_bucket") `data.lastEmptyBucket`: Option[
    String
  ] = None,
  @jsonField("data.last_sparse_bucket") `data.lastSparseBucket`: Option[
    String
  ] = None,
  @jsonField("model.bytes") `model.bytes`: Option[String] = None,
  @jsonField("model.memory_status") `model.memoryStatus`: Option[
    MemoryStatus
  ] = None,
  @jsonField("model.bytes_exceeded") `model.bytesExceeded`: Option[String] = None,
  @jsonField("model.memory_limit") `model.memoryLimit`: Option[String] = None,
  @jsonField("model.by_fields") `model.byFields`: Option[String] = None,
  @jsonField("model.over_fields") `model.overFields`: Option[String] = None,
  @jsonField("model.partition_fields") `model.partitionFields`: Option[
    String
  ] = None,
  @jsonField(
    "model.bucket_allocation_failures"
  ) `model.bucketAllocationFailures`: Option[String] = None,
  @jsonField(
    "model.categorization_status"
  ) `model.categorizationStatus`: Option[CategorizationStatus] = None,
  @jsonField(
    "model.categorized_doc_count"
  ) `model.categorizedDocCount`: Option[String] = None,
  @jsonField("model.total_category_count") `model.totalCategoryCount`: Option[
    String
  ] = None,
  @jsonField(
    "model.frequent_category_count"
  ) `model.frequentCategoryCount`: Option[String] = None,
  @jsonField("model.rare_category_count") `model.rareCategoryCount`: Option[
    String
  ] = None,
  @jsonField("model.dead_category_count") `model.deadCategoryCount`: Option[
    String
  ] = None,
  @jsonField(
    "model.failed_category_count"
  ) `model.failedCategoryCount`: Option[String] = None,
  @jsonField("model.log_time") `model.logTime`: Option[String] = None,
  @jsonField("model.timestamp") `model.timestamp`: Option[String] = None,
  @jsonField("forecasts.total") `forecasts.total`: Option[String] = None,
  @jsonField("forecasts.memory.min") `forecasts.memory.min`: Option[String] = None,
  @jsonField("forecasts.memory.max") `forecasts.memory.max`: Option[String] = None,
  @jsonField("forecasts.memory.avg") `forecasts.memory.avg`: Option[String] = None,
  @jsonField("forecasts.memory.total") `forecasts.memory.total`: Option[
    String
  ] = None,
  @jsonField("forecasts.records.min") `forecasts.records.min`: Option[
    String
  ] = None,
  @jsonField("forecasts.records.max") `forecasts.records.max`: Option[
    String
  ] = None,
  @jsonField("forecasts.records.avg") `forecasts.records.avg`: Option[
    String
  ] = None,
  @jsonField("forecasts.records.total") `forecasts.records.total`: Option[
    String
  ] = None,
  @jsonField("forecasts.time.min") `forecasts.time.min`: Option[String] = None,
  @jsonField("forecasts.time.max") `forecasts.time.max`: Option[String] = None,
  @jsonField("forecasts.time.avg") `forecasts.time.avg`: Option[String] = None,
  @jsonField("forecasts.time.total") `forecasts.time.total`: Option[String] = None,
  @jsonField("node.id") `node.id`: Option[String] = None,
  @jsonField("node.name") `node.name`: Option[String] = None,
  @jsonField("node.ephemeral_id") `node.ephemeralId`: Option[String] = None,
  @jsonField("node.address") `node.address`: Option[String] = None,
  @jsonField("buckets.count") `buckets.count`: Option[String] = None,
  @jsonField("buckets.time.total") `buckets.time.total`: Option[String] = None,
  @jsonField("buckets.time.min") `buckets.time.min`: Option[String] = None,
  @jsonField("buckets.time.max") `buckets.time.max`: Option[String] = None,
  @jsonField("buckets.time.exp_avg") `buckets.time.expAvg`: Option[String] = None,
  @jsonField("buckets.time.exp_avg_hour") `buckets.time.expAvgHour`: Option[
    String
  ] = None
)

object JobsRecord {
  implicit lazy val jsonCodec: JsonCodec[JobsRecord] =
    DeriveJsonCodec.gen[JobsRecord]
}
