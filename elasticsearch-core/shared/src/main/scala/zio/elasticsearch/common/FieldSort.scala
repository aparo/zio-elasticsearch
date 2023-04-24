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
import zio.elasticsearch.common.mappings.FieldType
import zio.elasticsearch.sort.{ SortMode, SortOrder }
import zio.json._
import zio.json.ast._
final case class FieldSort(
  missing: Option[Missing] = None,
  mode: Option[SortMode] = None,
  nested: Option[NestedSortValue] = None,
  order: Option[SortOrder] = None,
  @jsonField("unmapped_type") unmappedType: Option[FieldType] = None,
  @jsonField("numeric_type") numericType: Option[FieldSortNumericType] = None,
  format: Option[String] = None
)

object FieldSort {
  implicit lazy val jsonCodec: JsonCodec[FieldSort] = DeriveJsonCodec.gen[FieldSort]
}
