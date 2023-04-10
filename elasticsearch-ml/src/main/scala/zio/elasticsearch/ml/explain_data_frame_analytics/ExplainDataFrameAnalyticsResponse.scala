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

package zio.elasticsearch.ml.explain_data_frame_analytics
import zio._
import zio.elasticsearch.ml.{ DataframeAnalyticsFieldSelection, DataframeAnalyticsMemoryEstimation }
import zio.json._
import zio.json.ast._
/*
 * Explains a data frame analytics config.
 * For more info refers to http://www.elastic.co/guide/en/elasticsearch/reference/current/explain-dfanalytics.html
 *
 * @param fieldSelection An array of objects that explain selection for each field, sorted by the field names.

 * @param memoryEstimation An array of objects that explain selection for each field, sorted by the field names.

 */
final case class ExplainDataFrameAnalyticsResponse(
  fieldSelection: Chunk[DataframeAnalyticsFieldSelection] = Chunk.empty[DataframeAnalyticsFieldSelection],
  memoryEstimation: DataframeAnalyticsMemoryEstimation
) {}
object ExplainDataFrameAnalyticsResponse {
  implicit lazy val jsonCodec: JsonCodec[ExplainDataFrameAnalyticsResponse] =
    DeriveJsonCodec.gen[ExplainDataFrameAnalyticsResponse]
}
