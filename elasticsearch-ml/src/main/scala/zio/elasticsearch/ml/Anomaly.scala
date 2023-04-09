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
import zio._
import zio.json._
import zio.json.ast._
final case class Anomaly(
  actual: Option[Chunk[Double]] = None,
  @jsonField("anomaly_score_explanation") anomalyScoreExplanation: Option[
    AnomalyExplanation
  ] = None,
  @jsonField("bucket_span") bucketSpan: Long,
  @jsonField("by_field_name") byFieldName: Option[String] = None,
  @jsonField("by_field_value") byFieldValue: Option[String] = None,
  causes: Option[Chunk[AnomalyCause]] = None,
  @jsonField("detector_index") detectorIndex: Int,
  @jsonField("field_name") fieldName: Option[String] = None,
  function: Option[String] = None,
  @jsonField("function_description") functionDescription: Option[String] = None,
  @jsonField("geo_results") geoResults: Option[GeoResults] = None,
  influencers: Option[Chunk[Influence]] = None,
  @jsonField("initial_record_score") initialRecordScore: Double,
  @jsonField("is_interim") isInterim: Boolean,
  @jsonField("job_id") jobId: String,
  @jsonField("over_field_name") overFieldName: Option[String] = None,
  @jsonField("over_field_value") overFieldValue: Option[String] = None,
  @jsonField("partition_field_name") partitionFieldName: Option[String] = None,
  @jsonField("partition_field_value") partitionFieldValue: Option[String] = None,
  probability: Double,
  @jsonField("record_score") recordScore: Double,
  @jsonField("result_type") resultType: String,
  timestamp: Long,
  typical: Option[Chunk[Double]] = None
)

object Anomaly {
  implicit val jsonCodec: JsonCodec[Anomaly] = DeriveJsonCodec.gen[Anomaly]
}
