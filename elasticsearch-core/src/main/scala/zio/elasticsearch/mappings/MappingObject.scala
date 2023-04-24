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

package zio.elasticsearch.mappings

import zio.Chunk
import zio.elasticsearch.common.mappings.FieldType

trait MappingObject {
  def properties: Map[String, Mapping]

  def dynamicString: String

  def dynamicAsBoolean: Boolean = dynamicString.toLowerCase == "true"

  def enabled: Boolean

  def path: Option[String]

  def dottedFieldsRecursive(
    namedMapping: (String, Mapping),
    materialized: Boolean = false
  ): List[(String, FieldType)] = {
    val name = namedMapping._1
    val mapping = namedMapping._2
    val children = (mapping match {
      case o: MappingObject => o.properties
      case _                => Map.empty[String, Mapping]
    }).toList ++ mapping.subFields
    if (materialized && mapping.isInstanceOf[MappingObject])
      children.flatMap(c => dottedFieldsRecursive(c, materialized)).map(s => (s"$name.${s._1}", s._2))
    else
      (name -> mapping.`type`) :: children
        .flatMap(c => dottedFieldsRecursive(c, materialized))
        .map(s => (s"$name.${s._1}", s._2))
  }

  lazy val dottedFields: Chunk[String] = {
    Chunk.fromIterable(properties.flatMap(m => dottedFieldsRecursive(m)).keys.toList.sorted)
  }

  lazy val stringDottedFields: Chunk[String] = {
    Chunk.fromIterable(
      properties
        .flatMap(m => dottedFieldsRecursive(m))
        .filter(s => s._2 == FieldType.text || s._2 == FieldType.keyword || s._2 == FieldType.constant_keyword)
        .keys
        .toList
        .sorted
    )
  }

  lazy val geopointDottedFields: Chunk[String] = {
    Chunk.fromIterable(
      properties.flatMap(m => dottedFieldsRecursive(m)).filter(_._2 == FieldType.geo_point).keys.toList.sorted
    )
  }

  lazy val nestedDottedFields: Chunk[String] = {
    Chunk.fromIterable(
      properties.flatMap(m => dottedFieldsRecursive(m)).filter(_._2 == FieldType.nested).keys.toList.sorted
    )
  }

  lazy val dateDottedFields: Chunk[String] = {
    Chunk.fromIterable(
      properties
        .flatMap(m => dottedFieldsRecursive(m))
        .filter(s => s._2 == FieldType.date || s._2 == FieldType.date_nanos)
        .keys
        .toList
        .sorted
    )
  }

  /*Fields that are created in an index */
  def materializedDottedFields: Chunk[String] =
    Chunk.fromIterable(properties.flatMap(m => dottedFieldsRecursive(m, materialized = true)).keys.toList.sorted)

  def materializedTypedDottedFields: List[(String, FieldType)] =
    properties.flatMap(m => dottedFieldsRecursive(m, materialized = true)).toList.sortBy(f => (f._1, f._2.toString))

  lazy val parentTypes: Chunk[String] = Chunk.empty
  lazy val childTypes: Chunk[String] = Chunk.empty

}
