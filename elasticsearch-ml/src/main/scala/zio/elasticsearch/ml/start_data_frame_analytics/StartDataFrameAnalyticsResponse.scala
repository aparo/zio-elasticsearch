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

package zio.elasticsearch.ml.start_data_frame_analytics
import zio.json._
/*
 * Starts a data frame analytics job.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/start-dfanalytics.html
 *
 * @param acknowledged

 * @param node The ID of the node that the job was started on. If the job is allowed to open lazily and has not yet been assigned to a node, this value is an empty string.

 */
final case class StartDataFrameAnalyticsResponse(
  acknowledged: Boolean = true,
  node: String
) {}
object StartDataFrameAnalyticsResponse {
  implicit lazy val jsonCodec: JsonCodec[StartDataFrameAnalyticsResponse] =
    DeriveJsonCodec.gen[StartDataFrameAnalyticsResponse]
}
