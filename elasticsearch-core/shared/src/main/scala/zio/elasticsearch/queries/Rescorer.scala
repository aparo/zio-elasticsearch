/*
 * Copyright 2019 Alberto Paro
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

import zio.json._

final case class Rescorer(
  query: Query,
  @jsonField("rescore_query_weight") rescoreQueryWeight: Option[Float] = None,
  @jsonField("query_weight") queryWeight: Option[Float] = None,
  @jsonField("score_mode") scoreMode: Option[String] = None
)
object Rescorer {
  implicit val jsonDecoder: JsonDecoder[Rescorer] = DeriveJsonDecoder.gen[Rescorer]
  implicit val jsonEncoder: JsonEncoder[Rescorer] = DeriveJsonEncoder.gen[Rescorer]
}
