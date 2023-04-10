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

package zio.elasticsearch.ml.stop_data_frame_analytics
import zio.json._
import zio.json.ast._
/*
 * Stops one or more data frame analytics jobs.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/stop-dfanalytics.html
 *
 * @param stopped

 */
final case class StopDataFrameAnalyticsResponse(stopped: Boolean = true) {}
object StopDataFrameAnalyticsResponse {
  implicit lazy val jsonCodec: JsonCodec[StopDataFrameAnalyticsResponse] =
    DeriveJsonCodec.gen[StopDataFrameAnalyticsResponse]
}
