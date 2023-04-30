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
import zio._
import zio.json._
final case class FieldTypesMappings(
  @jsonField("field_types") fieldTypes: Chunk[FieldTypes],
  @jsonField("runtime_field_types") runtimeFieldTypes: Option[
    Chunk[RuntimeFieldTypes]
  ] = None,
  @jsonField("total_field_count") totalFieldCount: Option[Int] = None,
  @jsonField(
    "total_deduplicated_field_count"
  ) totalDeduplicatedFieldCount: Option[Int] = None,
  @jsonField(
    "total_deduplicated_mapping_size"
  ) totalDeduplicatedMappingSize: Option[String] = None,
  @jsonField(
    "total_deduplicated_mapping_size_in_bytes"
  ) totalDeduplicatedMappingSizeInBytes: Option[Long] = None
)

object FieldTypesMappings {
  implicit lazy val jsonCodec: JsonCodec[FieldTypesMappings] =
    DeriveJsonCodec.gen[FieldTypesMappings]
}
