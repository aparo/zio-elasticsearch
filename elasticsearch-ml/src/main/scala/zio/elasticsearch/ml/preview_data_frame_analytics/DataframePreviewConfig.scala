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

package zio.elasticsearch.ml.preview_data_frame_analytics
import zio.elasticsearch.ml.{ DataframeAnalysisAnalyzedFields, DataframeAnalysisContainer, DataframeAnalyticsSource }
import zio.json._
final case class DataframePreviewConfig(
  source: DataframeAnalyticsSource,
  analysis: DataframeAnalysisContainer,
  @jsonField("model_memory_limit") modelMemoryLimit: Option[String] = None,
  @jsonField("max_num_threads") maxNumThreads: Option[Int] = None,
  @jsonField("analyzed_fields") analyzedFields: Option[
    DataframeAnalysisAnalyzedFields
  ] = None
)

object DataframePreviewConfig {
  implicit lazy val jsonCodec: JsonCodec[DataframePreviewConfig] =
    DeriveJsonCodec.gen[DataframePreviewConfig]
}
