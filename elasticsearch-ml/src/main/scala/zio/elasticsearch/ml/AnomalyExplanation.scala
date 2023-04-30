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
import zio.json._
final case class AnomalyExplanation(
  @jsonField(
    "anomaly_characteristics_impact"
  ) anomalyCharacteristicsImpact: Option[Int] = None,
  @jsonField("anomaly_length") anomalyLength: Option[Int] = None,
  @jsonField("anomaly_type") anomalyType: Option[String] = None,
  @jsonField("high_variance_penalty") highVariancePenalty: Option[Boolean] = None,
  @jsonField("incomplete_bucket_penalty") incompleteBucketPenalty: Option[
    Boolean
  ] = None,
  @jsonField("lower_confidence_bound") lowerConfidenceBound: Option[Double] = None,
  @jsonField("multi_bucket_impact") multiBucketImpact: Option[Int] = None,
  @jsonField("single_bucket_impact") singleBucketImpact: Option[Int] = None,
  @jsonField("typical_value") typicalValue: Option[Double] = None,
  @jsonField("upper_confidence_bound") upperConfidenceBound: Option[Double] = None
)

object AnomalyExplanation {
  implicit lazy val jsonCodec: JsonCodec[AnomalyExplanation] =
    DeriveJsonCodec.gen[AnomalyExplanation]
}
