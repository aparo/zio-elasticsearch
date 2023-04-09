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

package zio.elasticsearch.ml.get_memory_stats
import zio.json._
import zio.json.ast._
final case class MemMlStats(
  @jsonField("anomaly_detectors") anomalyDetectors: Option[String] = None,
  @jsonField("anomaly_detectors_in_bytes") anomalyDetectorsInBytes: Int,
  @jsonField("data_frame_analytics") dataFrameAnalytics: Option[String] = None,
  @jsonField("data_frame_analytics_in_bytes") dataFrameAnalyticsInBytes: Int,
  max: Option[String] = None,
  @jsonField("max_in_bytes") maxInBytes: Int,
  @jsonField("native_code_overhead") nativeCodeOverhead: Option[String] = None,
  @jsonField("native_code_overhead_in_bytes") nativeCodeOverheadInBytes: Int,
  @jsonField("native_inference") nativeInference: Option[String] = None,
  @jsonField("native_inference_in_bytes") nativeInferenceInBytes: Int
)

object MemMlStats {
  implicit val jsonCodec: JsonCodec[MemMlStats] =
    DeriveJsonCodec.gen[MemMlStats]
}
