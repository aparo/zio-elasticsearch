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

package zio.elasticsearch.ml.info
import zio.elasticsearch.common.analysis._
import zio.elasticsearch.ml.CategorizationAnalyzer
import zio.json._
import zio.json.ast._
final case class AnomalyDetectors(
  @jsonField(
    "categorization_analyzer"
  ) categorizationAnalyzer: CategorizationAnalyzer,
  @jsonField(
    "categorization_examples_limit"
  ) categorizationExamplesLimit: Int,
  @jsonField("model_memory_limit") modelMemoryLimit: String,
  @jsonField("model_snapshot_retention_days") modelSnapshotRetentionDays: Int,
  @jsonField(
    "daily_model_snapshot_retention_after_days"
  ) dailyModelSnapshotRetentionAfterDays: Int
)

object AnomalyDetectors {
  implicit val jsonCodec: JsonCodec[AnomalyDetectors] =
    DeriveJsonCodec.gen[AnomalyDetectors]
}
