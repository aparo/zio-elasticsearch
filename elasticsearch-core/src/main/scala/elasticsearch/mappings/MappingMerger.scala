/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.mappings

import elasticsearch.common.circe.CirceUtils
import elasticsearch.exception.MergeMappingException
import elasticsearch.common.circe.diffson.circe._
import io.circe.Json
import io.circe.syntax._
import logstage.IzLogger

import scala.collection.mutable.ListBuffer

class MappingMerger(logger: IzLogger) {

  /**
    * Try to merge two mapping. Return new mapping or exception and way to fix in other case
    * @param sourceName the current mapping name
    * @param sourceMapping the source mapping
    * @param destName the other mapping name
    * @param destMapping the mapping to merge
    * @return The merged mapping or the an an Exception
    * */
  def merge(
      sourceName: String,
      sourceMapping: Mapping,
      destName: String,
      destMapping: Mapping
  ): (Seq[MergeMappingException], Option[Mapping]) =
    if (sourceMapping.`type` != destMapping.`type`) {
      Seq(
        MergeMappingException(
          "No compatible Mappings",
          s"The mappings of different types $sourceName - $destName are in conflict $sourceMapping != $destMapping"
        )
      ) -> None
    } else {
      sourceMapping.`type` match {
        case ObjectMapping.typeName =>
          val src = sourceMapping.asInstanceOf[ObjectMapping]
          val dest = destMapping.asInstanceOf[ObjectMapping]

          if (src == dest) { //fast check
            Nil -> Some(src)
          } else {
            val (errors, (newMapping, newProperties)) = mergePropertiesObject(
              sourceName,
              src.copy(properties = Map()),
              src.properties,
              destName,
              dest.copy(properties = Map()),
              dest.properties
            )
            errors -> newMapping.map(
              _.asInstanceOf[ObjectMapping].copy(properties = newProperties)
            )
          }

        case NestedMapping.typeName =>
          val src = sourceMapping.asInstanceOf[NestedMapping]
          val dest = destMapping.asInstanceOf[NestedMapping]

          if (src == dest) { //fast check
            Nil -> Some(src)
          } else {
            val (errors, (newMapping, newProperties)) = mergePropertiesObject(
              sourceName,
              src.copy(properties = Map()),
              src.properties,
              destName,
              dest.copy(properties = Map()),
              dest.properties
            )
            errors -> newMapping.map(
              _.asInstanceOf[NestedMapping].copy(properties = newProperties)
            )
          }

        case RootDocumentMapping.typeName =>
          val src = sourceMapping.asInstanceOf[RootDocumentMapping]
          val dest = destMapping.asInstanceOf[RootDocumentMapping]

          if (src == dest) { //fast check
            Nil -> Some(src)
          } else {
            val (errors, (newMapping, newProperties)) = mergePropertiesObject(
              sourceName,
              src.copy(properties = Map()),
              src.properties,
              destName,
              dest.copy(properties = Map()),
              dest.properties
            )
            errors -> newMapping.map(
              _.asInstanceOf[NestedMapping].copy(properties = newProperties)
            )
          }

        case _ =>
          mergeSimpleMappings(sourceName, sourceMapping, destName, destMapping)
      }
    }

  /**
    * Try to merge two mappings of Nested/Object/RootDocument. Return new mapping/properties or exception and way to fix in other case
    * @param sourceName the current mapping name
    * @param sourceMapping the source mapping
    * @param sourceProperties the source properties of the mapping
    * @param destName the other mapping name
    * @param destMapping the mapping to merge
    * @param destProperties the properties of the other mapping
    * @return The merged mapping or the an an Exception
    * */
  private def mergePropertiesObject(
      sourceName: String,
      sourceMapping: Mapping,
      sourceProperties: Map[String, Mapping],
      destName: String,
      destMapping: Mapping,
      destProperties: Map[String, Mapping]
  ): (Seq[MergeMappingException], (Option[Mapping], Map[String, Mapping])) = {
    //first we check mapping without properties
    val (simpleErrors, mapping) =
      mergeSimpleMappings(sourceName, sourceMapping, destName, destMapping)
    val (fieldsErrors, properties) =
      mergeMapMappings(sourceName, sourceProperties, destName, destProperties)
    simpleErrors ++ fieldsErrors -> (mapping -> properties)
  }

  private def mergeMapMappings(
      sourceName: String,
      sourceProperties: Map[String, Mapping],
      destName: String,
      destProperties: Map[String, Mapping]
  ): (Seq[MergeMappingException], Map[String, Mapping]) = {

    val propertiesToCheck =
      sourceProperties.keySet.intersect(destProperties.keySet)

    var newFields
      : List[(String, Mapping)] = sourceProperties.toList ++ destProperties.toList
    val errors = new ListBuffer[MergeMappingException]()

    if (propertiesToCheck.nonEmpty) {
      propertiesToCheck.foreach { propertyName =>
        val (mergeErrors, oproperty) =
          merge(
            s"$sourceName.$propertyName",
            sourceProperties(propertyName),
            s"$destName.$propertyName",
            destProperties(propertyName)
          )
        oproperty.foreach(prop => newFields ::= (propertyName -> prop))
        if (mergeErrors.nonEmpty) {
          errors ++= mergeErrors
        }
      }
    }
    errors -> newFields.toMap
  }

  private def mergeSimpleMappings(
      sourceName: String,
      sourceMapping: Mapping,
      destName: String,
      destMapping: Mapping
  ): (Seq[MergeMappingException], Option[Mapping]) = {
    var source = sourceMapping.asJson
    val patch = JsonDiff.diff(source, destMapping.asJson, true)
    if (patch.ops.isEmpty) {
      Nil -> Some(sourceMapping)
    } else {
      //we process fields once

      var fieldsProcessed = false

      val errors = new ListBuffer[MergeMappingException]()

      def mergeFields(): Unit =
        // we process fields once
        if (!fieldsProcessed) {
          val (mergeErrors, fields) = mergeMapMappings(
            sourceName + ".fields",
            sourceMapping.fields,
            destName + ".fields",
            destMapping.fields
          )
          errors ++= mergeErrors
          source = Json.fromJsonObject(
            source.asObject.get
              .add("fields", CirceUtils.joClean(fields.asJson))
          )
          fieldsProcessed = true
        }

      patch.foreach {
        case op @ Replace(path, value, old) =>
          val realPath = path.serialize.substring(1)
          if (realPath == "type") {
            errors += MergeMappingException(
              "No compatible Mappings",
              s"The mappings of different types $sourceName - $destName are in conflict $sourceMapping != $destMapping"
            )
          } else if (realPath.startsWith("fields/")) {
            mergeFields()
          } else {
            if (value.isNull) {
              //we skip null value
              logger.debug(
                s"Mapping Merge: $destName $realPath should be set to ${old.get}"
              )
            } else {
              old.foreach { oldValue =>
                if (oldValue.isNull) {
                  //we can replace without conflicts
                  logger.debug(
                    s"Mapping Merge: $sourceName $realPath will be set to $value"
                  )
                  source = op(source)
                } else {
                  errors += MergeMappingException(
                    "No compatible Mappings",
                    s"The mappings of values of $sourceName.$realPath - $destName.$realPath are in conflict $oldValue != $value"
                  )
                }
              }

            }

          }

        case Add(path, _) =>
          val realpath = path.serialize.substring(1)
          if (realpath.startsWith("fields/")) mergeFields()
        case Copy(_, _) =>
        case Move(_, _) =>
        case Remove(path, _) =>
          val realpath = path.serialize.substring(1)
          if (realpath.startsWith("fields/")) mergeFields()

        case Test(_, _) =>
      }
      source =
        Json.fromJsonObject(source.asObject.get.filter(v => !v._2.isNull))
      errors -> source.as[Mapping].toOption
    }
  }
}
