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

package zio.elasticsearch.cluster.stats
import zio.json._
final case class FieldTypes(
  name: String,
  count: Int,
  @jsonField("index_count") indexCount: Int,
  @jsonField("indexed_vector_count") indexedVectorCount: Option[Long] = None,
  @jsonField("indexed_vector_dim_max") indexedVectorDimMax: Option[Long] = None,
  @jsonField("indexed_vector_dim_min") indexedVectorDimMin: Option[Long] = None,
  @jsonField("script_count") scriptCount: Option[Int] = None
)

object FieldTypes {
  implicit lazy val jsonCodec: JsonCodec[FieldTypes] =
    DeriveJsonCodec.gen[FieldTypes]
}
