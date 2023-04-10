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

package zio.elasticsearch.ml
import zio._
import zio.json._
import zio.json.ast._
final case class AnomalyCause(
  actual: Chunk[Double],
  @jsonField("by_field_name") byFieldName: String,
  @jsonField("by_field_value") byFieldValue: String,
  @jsonField("correlated_by_field_value") correlatedByFieldValue: String,
  @jsonField("field_name") fieldName: String,
  function: String,
  @jsonField("function_description") functionDescription: String,
  influencers: Chunk[Influence],
  @jsonField("over_field_name") overFieldName: String,
  @jsonField("over_field_value") overFieldValue: String,
  @jsonField("partition_field_name") partitionFieldName: String,
  @jsonField("partition_field_value") partitionFieldValue: String,
  probability: Double,
  typical: Chunk[Double]
)

object AnomalyCause {
  implicit lazy val jsonCodec: JsonCodec[AnomalyCause] =
    DeriveJsonCodec.gen[AnomalyCause]
}
