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

package zio.elasticsearch.queries

import zio.elasticsearch.sort.Sort.Sort
import zio.json._

final case class InnerHits(
  name: String,
  @jsonField("ignore_unmapped") ignoreUnmapped: Option[Boolean] = None,
  version: Boolean = false,
  explain: Boolean = false,
  @jsonField("track_scores") trackScores: Boolean = false,
  from: Int = 0,
  size: Int = 10,
  sort: Sort
)
object InnerHits {
  implicit val jsonDecoder: JsonDecoder[InnerHits] = DeriveJsonDecoder.gen[InnerHits]
  implicit val jsonEncoder: JsonEncoder[InnerHits] = DeriveJsonEncoder.gen[InnerHits]
}
