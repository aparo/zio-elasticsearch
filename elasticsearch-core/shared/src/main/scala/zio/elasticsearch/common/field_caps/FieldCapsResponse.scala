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

package zio.elasticsearch.common.field_caps
import zio._
import zio.json._
import zio.json.ast._
/*
 * Returns the information about the capabilities of fields among multiple indices.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-field-caps.html
 *
 * @param indices

 * @param fields

 */
final case class FieldCapsResponse(
  indices: Chunk[String] = Chunk.empty[String],
  fields: Map[String, Map[String, FieldCapability]] = Map.empty[String, Map[String, FieldCapability]]
) {}
object FieldCapsResponse {
  implicit val jsonCodec: JsonCodec[FieldCapsResponse] =
    DeriveJsonCodec.gen[FieldCapsResponse]
}
