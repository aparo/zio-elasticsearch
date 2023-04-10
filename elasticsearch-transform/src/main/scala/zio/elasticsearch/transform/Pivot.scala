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

package zio.elasticsearch.transform
import zio.elasticsearch.aggregations.Aggregation
import zio.json._
import zio.json.ast._
final case class Pivot(
  aggregations: Option[Map[String, Aggregation]] = None,
  @jsonField("group_by") groupBy: Option[Map[String, PivotGroupByContainer]] = None
)

object Pivot {
  implicit val jsonCodec: JsonCodec[Pivot] = DeriveJsonCodec.gen[Pivot]
}