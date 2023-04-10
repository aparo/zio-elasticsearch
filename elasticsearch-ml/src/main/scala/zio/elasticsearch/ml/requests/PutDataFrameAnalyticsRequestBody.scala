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

package zio.elasticsearch.ml.requests
import zio.elasticsearch.ml._
import zio.json._
import zio.json.ast._

final case class PutDataFrameAnalyticsRequestBody(
  @jsonField("allow_lazy_start") allowLazyStart: Option[Boolean] = None,
  analysis: DataframeAnalysisContainer,
  @jsonField("analyzed_fields") analyzedFields: Option[
    DataframeAnalysisAnalyzedFields
  ] = None,
  description: Option[String] = None,
  dest: DataframeAnalyticsDestination,
  @jsonField("max_num_threads") maxNumThreads: Option[Int] = None,
  @jsonField("model_memory_limit") modelMemoryLimit: Option[String] = None,
  source: DataframeAnalyticsSource,
  headers: Option[Json] = None,
  version: Option[String] = None
)

object PutDataFrameAnalyticsRequestBody {
  implicit lazy val jsonCodec: JsonCodec[PutDataFrameAnalyticsRequestBody] =
    DeriveJsonCodec.gen[PutDataFrameAnalyticsRequestBody]
}
