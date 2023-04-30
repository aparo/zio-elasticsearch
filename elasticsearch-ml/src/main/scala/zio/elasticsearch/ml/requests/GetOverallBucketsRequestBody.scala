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
import java.time._

import zio.json._
import zio.json.ast._

final case class GetOverallBucketsRequestBody(
  @jsonField("allow_no_match") allowNoMatch: Option[Boolean] = None,
  @jsonField("bucket_span") bucketSpan: Option[String] = None,
  end: Option[LocalDateTime] = None,
  @jsonField("exclude_interim") excludeInterim: Option[Boolean] = None,
  @jsonField("overall_score") overallScore: Option[Json] = None,
  start: Option[LocalDateTime] = None,
  @jsonField("top_n") topN: Option[Int] = None
)

object GetOverallBucketsRequestBody {
  implicit lazy val jsonCodec: JsonCodec[GetOverallBucketsRequestBody] =
    DeriveJsonCodec.gen[GetOverallBucketsRequestBody]
}
