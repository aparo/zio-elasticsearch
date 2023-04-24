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

package zio.elasticsearch.cat

import zio.json._

sealed trait CatAnomalyDetectorColumn

object CatAnomalyDetectorColumn {

  case object assignment_explanation extends CatAnomalyDetectorColumn

  case object ae extends CatAnomalyDetectorColumn

  case object `buckets.count` extends CatAnomalyDetectorColumn

  case object bc extends CatAnomalyDetectorColumn

  case object bucketsCount extends CatAnomalyDetectorColumn

  case object `buckets.time.exp_avg` extends CatAnomalyDetectorColumn

  case object btea extends CatAnomalyDetectorColumn

  case object bucketsTimeExpAvg extends CatAnomalyDetectorColumn

  case object `buckets.time.exp_avg_hour` extends CatAnomalyDetectorColumn

  case object bteah extends CatAnomalyDetectorColumn

  case object bucketsTimeExpAvgHour extends CatAnomalyDetectorColumn

  case object `buckets.time.max` extends CatAnomalyDetectorColumn

  case object btmax extends CatAnomalyDetectorColumn

  case object bucketsTimeMax extends CatAnomalyDetectorColumn

  case object `buckets.time.min` extends CatAnomalyDetectorColumn

  case object btmin extends CatAnomalyDetectorColumn

  case object bucketsTimeMin extends CatAnomalyDetectorColumn

  case object `buckets.time.total` extends CatAnomalyDetectorColumn

  case object btt extends CatAnomalyDetectorColumn

  case object bucketsTimeTotal extends CatAnomalyDetectorColumn

  case object `data.buckets` extends CatAnomalyDetectorColumn

  case object db extends CatAnomalyDetectorColumn

  case object dataBuckets extends CatAnomalyDetectorColumn

  case object `data.earliest_record` extends CatAnomalyDetectorColumn

  case object der extends CatAnomalyDetectorColumn

  case object dataEarliestRecord extends CatAnomalyDetectorColumn

  case object `data.empty_buckets` extends CatAnomalyDetectorColumn

  case object deb extends CatAnomalyDetectorColumn

  case object dataEmptyBuckets extends CatAnomalyDetectorColumn

  case object `data.input_bytes` extends CatAnomalyDetectorColumn

  case object dib extends CatAnomalyDetectorColumn

  case object dataInputBytes extends CatAnomalyDetectorColumn

  case object `data.input_fields` extends CatAnomalyDetectorColumn

  case object dif extends CatAnomalyDetectorColumn

  case object dataInputFields extends CatAnomalyDetectorColumn

  case object `data.input_records` extends CatAnomalyDetectorColumn

  case object dir extends CatAnomalyDetectorColumn

  case object dataInputRecords extends CatAnomalyDetectorColumn

  case object `data.invalid_dates` extends CatAnomalyDetectorColumn

  case object did extends CatAnomalyDetectorColumn

  case object dataInvalidDates extends CatAnomalyDetectorColumn

  case object `data.last` extends CatAnomalyDetectorColumn

  case object dl extends CatAnomalyDetectorColumn

  case object dataLast extends CatAnomalyDetectorColumn

  case object `data.last_empty_bucket` extends CatAnomalyDetectorColumn

  case object dleb extends CatAnomalyDetectorColumn

  case object dataLastEmptyBucket extends CatAnomalyDetectorColumn

  case object `data.last_sparse_bucket` extends CatAnomalyDetectorColumn

  case object dlsb extends CatAnomalyDetectorColumn

  case object dataLastSparseBucket extends CatAnomalyDetectorColumn

  case object `data.latest_record` extends CatAnomalyDetectorColumn

  case object dlr extends CatAnomalyDetectorColumn

  case object dataLatestRecord extends CatAnomalyDetectorColumn

  case object `data.missing_fields` extends CatAnomalyDetectorColumn

  case object dmf extends CatAnomalyDetectorColumn

  case object dataMissingFields extends CatAnomalyDetectorColumn

  case object `data.out_of_order_timestamps` extends CatAnomalyDetectorColumn

  case object doot extends CatAnomalyDetectorColumn

  case object dataOutOfOrderTimestamps extends CatAnomalyDetectorColumn

  case object `data.processed_fields` extends CatAnomalyDetectorColumn

  case object dpf extends CatAnomalyDetectorColumn

  case object dataProcessedFields extends CatAnomalyDetectorColumn

  case object `data.processed_records` extends CatAnomalyDetectorColumn

  case object dpr extends CatAnomalyDetectorColumn

  case object dataProcessedRecords extends CatAnomalyDetectorColumn

  case object `data.sparse_buckets` extends CatAnomalyDetectorColumn

  case object dsb extends CatAnomalyDetectorColumn

  case object dataSparseBuckets extends CatAnomalyDetectorColumn

  case object `forecasts.memory.avg` extends CatAnomalyDetectorColumn

  case object fmavg extends CatAnomalyDetectorColumn

  case object forecastsMemoryAvg extends CatAnomalyDetectorColumn

  case object `forecasts.memory.max` extends CatAnomalyDetectorColumn

  case object fmmax extends CatAnomalyDetectorColumn

  case object forecastsMemoryMax extends CatAnomalyDetectorColumn

  case object `forecasts.memory.min` extends CatAnomalyDetectorColumn

  case object fmmin extends CatAnomalyDetectorColumn

  case object forecastsMemoryMin extends CatAnomalyDetectorColumn

  case object `forecasts.memory.total` extends CatAnomalyDetectorColumn

  case object fmt extends CatAnomalyDetectorColumn

  case object forecastsMemoryTotal extends CatAnomalyDetectorColumn

  case object `forecasts.records.avg` extends CatAnomalyDetectorColumn

  case object fravg extends CatAnomalyDetectorColumn

  case object forecastsRecordsAvg extends CatAnomalyDetectorColumn

  case object `forecasts.records.max` extends CatAnomalyDetectorColumn

  case object frmax extends CatAnomalyDetectorColumn

  case object forecastsRecordsMax extends CatAnomalyDetectorColumn

  case object `forecasts.records.min` extends CatAnomalyDetectorColumn

  case object frmin extends CatAnomalyDetectorColumn

  case object forecastsRecordsMin extends CatAnomalyDetectorColumn

  case object `forecasts.records.total` extends CatAnomalyDetectorColumn

  case object frt extends CatAnomalyDetectorColumn

  case object forecastsRecordsTotal extends CatAnomalyDetectorColumn

  case object `forecasts.time.avg` extends CatAnomalyDetectorColumn

  case object ftavg extends CatAnomalyDetectorColumn

  case object forecastsTimeAvg extends CatAnomalyDetectorColumn

  case object `forecasts.time.max` extends CatAnomalyDetectorColumn

  case object ftmax extends CatAnomalyDetectorColumn

  case object forecastsTimeMax extends CatAnomalyDetectorColumn

  case object `forecasts.time.min` extends CatAnomalyDetectorColumn

  case object ftmin extends CatAnomalyDetectorColumn

  case object forecastsTimeMin extends CatAnomalyDetectorColumn

  case object `forecasts.time.total` extends CatAnomalyDetectorColumn

  case object ftt extends CatAnomalyDetectorColumn

  case object forecastsTimeTotal extends CatAnomalyDetectorColumn

  case object `forecasts.total` extends CatAnomalyDetectorColumn

  case object ft extends CatAnomalyDetectorColumn

  case object forecastsTotal extends CatAnomalyDetectorColumn

  case object id extends CatAnomalyDetectorColumn

  case object `model.bucket_allocation_failures` extends CatAnomalyDetectorColumn

  case object mbaf extends CatAnomalyDetectorColumn

  case object modelBucketAllocationFailures extends CatAnomalyDetectorColumn

  case object `model.by_fields` extends CatAnomalyDetectorColumn

  case object mbf extends CatAnomalyDetectorColumn

  case object modelByFields extends CatAnomalyDetectorColumn

  case object `model.bytes` extends CatAnomalyDetectorColumn

  case object mb extends CatAnomalyDetectorColumn

  case object modelBytes extends CatAnomalyDetectorColumn

  case object `model.bytes_exceeded` extends CatAnomalyDetectorColumn

  case object mbe extends CatAnomalyDetectorColumn

  case object modelBytesExceeded extends CatAnomalyDetectorColumn

  case object `model.categorization_status` extends CatAnomalyDetectorColumn

  case object mcs extends CatAnomalyDetectorColumn

  case object modelCategorizationStatus extends CatAnomalyDetectorColumn

  case object `model.categorized_doc_count` extends CatAnomalyDetectorColumn

  case object mcdc extends CatAnomalyDetectorColumn

  case object modelCategorizedDocCount extends CatAnomalyDetectorColumn

  case object `model.dead_category_count` extends CatAnomalyDetectorColumn

  case object mdcc extends CatAnomalyDetectorColumn

  case object modelDeadCategoryCount extends CatAnomalyDetectorColumn

  case object `model.failed_category_count` extends CatAnomalyDetectorColumn

//  case object mdcc extends CatAnomalyDetectorColumn

  case object modelFailedCategoryCount extends CatAnomalyDetectorColumn

  case object `model.frequent_category_count` extends CatAnomalyDetectorColumn

  case object mfcc extends CatAnomalyDetectorColumn

  case object modelFrequentCategoryCount extends CatAnomalyDetectorColumn

  case object `model.log_time` extends CatAnomalyDetectorColumn

  case object mlt extends CatAnomalyDetectorColumn

  case object modelLogTime extends CatAnomalyDetectorColumn

  case object `model.memory_limit` extends CatAnomalyDetectorColumn

  case object mml extends CatAnomalyDetectorColumn

  case object modelMemoryLimit extends CatAnomalyDetectorColumn

  case object `model.memory_status` extends CatAnomalyDetectorColumn

  case object mms extends CatAnomalyDetectorColumn

  case object modelMemoryStatus extends CatAnomalyDetectorColumn

  case object `model.over_fields` extends CatAnomalyDetectorColumn

  case object mof extends CatAnomalyDetectorColumn

  case object modelOverFields extends CatAnomalyDetectorColumn

  case object `model.partition_fields` extends CatAnomalyDetectorColumn

  case object mpf extends CatAnomalyDetectorColumn

  case object modelPartitionFields extends CatAnomalyDetectorColumn

  case object `model.rare_category_count` extends CatAnomalyDetectorColumn

  case object mrcc extends CatAnomalyDetectorColumn

  case object modelRareCategoryCount extends CatAnomalyDetectorColumn

  case object `model.timestamp` extends CatAnomalyDetectorColumn

  case object mt extends CatAnomalyDetectorColumn

  case object modelTimestamp extends CatAnomalyDetectorColumn

  case object `model.total_category_count` extends CatAnomalyDetectorColumn

  case object mtcc extends CatAnomalyDetectorColumn

  case object modelTotalCategoryCount extends CatAnomalyDetectorColumn

  case object `node.address` extends CatAnomalyDetectorColumn

  case object na extends CatAnomalyDetectorColumn

  case object nodeAddress extends CatAnomalyDetectorColumn

  case object `node.ephemeral_id` extends CatAnomalyDetectorColumn

  case object ne extends CatAnomalyDetectorColumn

  case object nodeEphemeralId extends CatAnomalyDetectorColumn

  case object `node.id` extends CatAnomalyDetectorColumn

  case object ni extends CatAnomalyDetectorColumn

  case object nodeId extends CatAnomalyDetectorColumn

  case object `node.name` extends CatAnomalyDetectorColumn

  case object nn extends CatAnomalyDetectorColumn

  case object nodeName extends CatAnomalyDetectorColumn

  case object opened_time extends CatAnomalyDetectorColumn

  case object ot extends CatAnomalyDetectorColumn

  case object state extends CatAnomalyDetectorColumn

  case object s extends CatAnomalyDetectorColumn

  implicit final val decoder: JsonDecoder[CatAnomalyDetectorColumn] =
    DeriveJsonDecoderEnum.gen[CatAnomalyDetectorColumn]
  implicit final val encoder: JsonEncoder[CatAnomalyDetectorColumn] =
    DeriveJsonEncoderEnum.gen[CatAnomalyDetectorColumn]
  implicit final val codec: JsonCodec[CatAnomalyDetectorColumn] =
    JsonCodec(encoder, decoder)

}
