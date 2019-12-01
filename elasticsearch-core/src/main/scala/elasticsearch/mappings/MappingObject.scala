/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.mappings

trait MappingObject {
  def properties: Map[String, Mapping]

  def dynamicString: String

  def dynamicAsBoolean: Boolean = dynamicString.toLowerCase == "true"

  def enabled: Boolean

  def path: Option[String]

  def dottedFieldsRecursive(
      namedMapping: (String, Mapping),
      materialized: Boolean = false
  ): List[(String, String)] = {
    val name = namedMapping._1
    val mapping = namedMapping._2
    val children = (mapping match {
      case o: MappingObject => o.properties
      case _ => Map.empty[String, Mapping]
    }).toList ++ mapping.subFields
    if (materialized && mapping.isInstanceOf[MappingObject])
      children
        .flatMap(c => dottedFieldsRecursive(c, materialized))
        .map(s => (s"$name.${s._1}", s._2))
    else
      (name -> mapping.`type`) :: children
        .flatMap(c => dottedFieldsRecursive(c, materialized))
        .map(s => (s"$name.${s._1}", s._2))
  }

  lazy val dottedFields: List[String] = {
    properties.flatMap(m => dottedFieldsRecursive(m)).keys.toList.sorted
  }

  lazy val stringDottedFields: List[String] = {
    properties
      .flatMap(m => dottedFieldsRecursive(m))
      .filter(
        s => s._2 == TextMapping.typeName || s._2 == KeywordMapping.typeName
      )
      .keys
      .toList
      .sorted
  }

  lazy val geopointDottedFields: List[String] = {
    properties
      .flatMap(m => dottedFieldsRecursive(m))
      .filter(_._2 == GeoPointMapping.typeName)
      .keys
      .toList
      .sorted
  }

  lazy val nestedDottedFields: List[String] = {
    properties
      .flatMap(m => dottedFieldsRecursive(m))
      .filter(_._2 == NestedMapping.typeName)
      .keys
      .toList
      .sorted
  }

  lazy val dateDottedFields: List[String] = {
    properties
      .flatMap(m => dottedFieldsRecursive(m))
      .filter(_._2 == DateTimeMapping.typeName)
      .keys
      .toList
      .sorted
  }

  /*Fields that are created in an index */
  def materializedDottedFields: List[String] =
    properties
      .flatMap(m => dottedFieldsRecursive(m, materialized = true))
      .keys
      .toList
      .sorted

  def materializedTypedDottedFields: List[(String, String)] =
    properties
      .flatMap(m => dottedFieldsRecursive(m, materialized = true))
      .toList
      .sorted

  lazy val parentTypes: List[String] = Nil
  lazy val childTypes: List[String] = Nil

}
