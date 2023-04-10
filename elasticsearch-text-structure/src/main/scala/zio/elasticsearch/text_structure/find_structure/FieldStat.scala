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

package zio.elasticsearch.text_structure.find_structure
import zio._
import zio.json._
import zio.json.ast._
final case class FieldStat(
  count: Int,
  cardinality: Int,
  @jsonField("top_hits") topHits: Chunk[TopHit],
  @jsonField("mean_value") meanValue: Option[Int] = None,
  @jsonField("median_value") medianValue: Option[Int] = None,
  @jsonField("max_value") maxValue: Option[Int] = None,
  @jsonField("min_value") minValue: Option[Int] = None,
  earliest: Option[String] = None,
  latest: Option[String] = None
)

object FieldStat {
  implicit lazy val jsonCodec: JsonCodec[FieldStat] = DeriveJsonCodec.gen[FieldStat]
}
