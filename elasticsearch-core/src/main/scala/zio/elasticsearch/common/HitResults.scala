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

package zio.elasticsearch.common

import zio.Chunk
import zio.json.{ DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder, jsonField }

final case class Total(value: Long = 0, relation: String = "eq")
object Total {
  implicit val jsonDecoder: JsonDecoder[Total] = DeriveJsonDecoder.gen[Total]
  implicit val jsonEncoder: JsonEncoder[Total] = DeriveJsonEncoder.gen[Total]
}

final case class HitResults(
  total: Option[Total] = None,
  @jsonField("max_score") maxScore: Option[Double] = None,
  hits: Chunk[ResultDocument] = Chunk.empty[ResultDocument]
)

object HitResults {
  val empty = new HitResults()
  implicit val jsonDecoder: JsonDecoder[HitResults] = DeriveJsonDecoder.gen[HitResults]
  implicit val jsonEncoder: JsonEncoder[HitResults] = DeriveJsonEncoder.gen[HitResults]

}
