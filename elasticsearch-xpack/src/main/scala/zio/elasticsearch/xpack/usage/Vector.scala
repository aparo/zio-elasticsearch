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

package zio.elasticsearch.xpack.usage
import zio.json._
import zio.json.ast._
final case class Vector(
  @jsonField("dense_vector_dims_avg_count") denseVectorDimsAvgCount: Int,
  @jsonField("dense_vector_fields_count") denseVectorFieldsCount: Int,
  @jsonField("sparse_vector_fields_count") sparseVectorFieldsCount: Option[
    Int
  ] = None,
  available: Boolean,
  enabled: Boolean
)

object Vector {
  implicit lazy val jsonCodec: JsonCodec[Vector] = DeriveJsonCodec.gen[Vector]
}
