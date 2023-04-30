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

package zio.elasticsearch.ml.get_data_frame_analytics
import zio._
import zio.elasticsearch.ml.DataframeAnalytics
import zio.json._
/*
 * Retrieves configuration information for data frame analytics jobs.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-dfanalytics.html
 *
 * @param count

 * @param dataFrameAnalytics An array of objects that contain usage information for data frame analytics jobs, which are sorted by the id value in ascending order.

 */
final case class GetDataFrameAnalyticsResponse(
  count: Long,
  dataFrameAnalytics: Chunk[DataframeAnalytics] = Chunk.empty[DataframeAnalytics]
) {}
object GetDataFrameAnalyticsResponse {
  implicit lazy val jsonCodec: JsonCodec[GetDataFrameAnalyticsResponse] =
    DeriveJsonCodec.gen[GetDataFrameAnalyticsResponse]
}
