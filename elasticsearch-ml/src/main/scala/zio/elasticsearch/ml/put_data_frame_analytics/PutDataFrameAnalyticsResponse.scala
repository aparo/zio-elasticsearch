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

package zio.elasticsearch.ml.put_data_frame_analytics
import zio.elasticsearch.ml._
import zio.json._
/*
 * Instantiates a data frame analytics job.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/put-dfanalytics.html
 *
 * @param authorization

 * @param allowLazyStart

 * @param analysis

 * @param analyzedFields

 * @param createTime

 * @param description

 * @param dest

 * @param id

 * @param maxNumThreads

 * @param modelMemoryLimit

 * @param source

 * @param version

 */
final case class PutDataFrameAnalyticsResponse(
  authorization: DataframeAnalyticsAuthorization,
  allowLazyStart: Boolean = true,
  analysis: DataframeAnalysisContainer,
  analyzedFields: DataframeAnalysisAnalyzedFields,
  createTime: Long,
  description: String,
  dest: DataframeAnalyticsDestination,
  id: String,
  maxNumThreads: Int,
  modelMemoryLimit: String,
  source: DataframeAnalyticsSource,
  version: String
) {}
object PutDataFrameAnalyticsResponse {
  implicit lazy val jsonCodec: JsonCodec[PutDataFrameAnalyticsResponse] =
    DeriveJsonCodec.gen[PutDataFrameAnalyticsResponse]
}
