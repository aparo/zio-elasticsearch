/*
 * Copyright 2019-2020 Alberto Paro
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

package elasticsearch.mappings

import zio.circe.CirceUtils
import zio.exception.MergeMappingException
import zio.circe.diffson.circe._
import io.circe.Json
import io.circe.syntax._
import cats.implicits._
import zio.UIO
import zio.logging.{LogLevel, Logging}

import scala.collection.mutable.ListBuffer

class MappingMerger(loggingService: Logging.Service) {
  def logDebug(s: => String): UIO[Unit] =
    loggingService.logger.log(LogLevel.Debug)(s)

  def merge(schemaMappings: List[(String, Mapping)])
    : Either[List[MergeMappingException], Mapping] =
    if (schemaMappings.isEmpty) Right(RootDocumentMapping())
    else if (schemaMappings.length == 1) Right(schemaMappings.head._2)
    else {
      val (fname, fmapping) = schemaMappings.head
      var mapping = fmapping
      val errors = new ListBuffer[MergeMappingException]
      schemaMappings.tail.foreach {
        case (nName, nMapping) =>
          merge(fname, mapping, nName, nMapping) match {
            case Left(newErrors) => errors ++= newErrors
            case Right(newMapping) => mapping = newMapping
          }
      }
      if (errors.nonEmpty) Left(errors.toList)
      else Right(mapping)
    }

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
  ): Either[List[MergeMappingException], Mapping] =
    if (sourceMapping.`type` != destMapping.`type`) {
      Left(
        List(
          MergeMappingException(
            "No compatible Mappings",
            s"The mappings of different types $sourceName - $destName are in conflict $sourceMapping != $destMapping"
          )
        )
      )
    } else {
      sourceMapping.`type` match {
        case ObjectMapping.typeName =>
          val src = sourceMapping.asInstanceOf[ObjectMapping]
          val dest = destMapping.asInstanceOf[ObjectMapping]

          if (src == dest) { //fast check
            Right(src)
          } else {
            for {
              newMapping <- mergePropertiesObject(
                sourceName,
                src.copy(properties = Map()),
                src.properties,
                destName,
                dest.copy(properties = Map()),
                dest.properties
              )
            } yield
              newMapping._1
                .asInstanceOf[ObjectMapping]
                .copy(properties = newMapping._2)
          }

        case NestedMapping.typeName =>
          val src = sourceMapping.asInstanceOf[NestedMapping]
          val dest = destMapping.asInstanceOf[NestedMapping]

          if (src == dest) { //fast check
            Right(src)
          } else {
            val result = mergePropertiesObject(
              sourceName,
              src.copy(properties = Map()),
              src.properties,
              destName,
              dest.copy(properties = Map()),
              dest.properties
            )
            result.map {
              case (mapping, newProperties) =>
                mapping
                  .asInstanceOf[NestedMapping]
                  .copy(properties = newProperties)
            }
          }

        case RootDocumentMapping.typeName =>
          val src = sourceMapping.asInstanceOf[RootDocumentMapping]
          val dest = destMapping.asInstanceOf[RootDocumentMapping]

          if (src == dest) { //fast check
            Right(src)
          } else {
            val result = mergePropertiesObject(
              sourceName,
              src.copy(properties = Map()),
              src.properties,
              destName,
              dest.copy(properties = Map()),
              dest.properties
            )
            result.map {
              case (mapping, newProperties) =>
                mapping
                  .asInstanceOf[RootDocumentMapping]
                  .copy(properties = newProperties)
            }
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
  ): Either[List[MergeMappingException], (Mapping, Map[String, Mapping])] = {
    //first we check mapping without properties
    val result =
      mergeSimpleMappings(sourceName, sourceMapping, destName, destMapping)
    val (fieldsErrors, properties) =
      mergeMapMappings(sourceName, sourceProperties, destName, destProperties)
    if (fieldsErrors.nonEmpty)
      Left(fieldsErrors)
    else
      result match {
        case Left(simpleErrors) =>
          Left(simpleErrors ++ fieldsErrors)
        case Right(mapping) =>
          Right(mapping -> properties)
      }
  }

  private def mergeMapMappings(
      sourceName: String,
      sourceProperties: Map[String, Mapping],
      destName: String,
      destProperties: Map[String, Mapping]
  ): (List[MergeMappingException], Map[String, Mapping]) = {

    val propertiesToCheck =
      sourceProperties.keySet.intersect(destProperties.keySet)

    var newFields
      : List[(String, Mapping)] = sourceProperties.toList ++ destProperties.toList
    val errors = new ListBuffer[MergeMappingException]()

    if (propertiesToCheck.nonEmpty) {
      propertiesToCheck.foreach { propertyName =>
        val result =
          merge(
            s"$sourceName.$propertyName",
            sourceProperties(propertyName),
            s"$destName.$propertyName",
            destProperties(propertyName)
          )
        result match {
          case Left(mergeErrors) =>
            errors ++= mergeErrors
          case Right(prop) =>
            newFields ::= (propertyName -> prop)
        }
      }
    }
    errors.toList -> newFields.toMap
  }

  private def mergeSimpleMappings(
      sourceName: String,
      sourceMapping: Mapping,
      destName: String,
      destMapping: Mapping
  ): Either[List[MergeMappingException], Mapping] = {
    var source = sourceMapping.asJson
    val patch = JsonDiff.diff(source, destMapping.asJson, true)
    if (patch.ops.isEmpty) {
      Right(sourceMapping)
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
              logDebug(
                s"Mapping Merge: $destName $realPath should be set to ${old.get}"
              )
            } else {
              old.foreach { oldValue =>
                if (oldValue.isNull) {
                  //we can replace without conflicts
                  logDebug(
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
      if (errors.nonEmpty) Left(errors.toList)
      else
        source
          .as[Mapping]
          .leftMap(c =>
            List(MergeMappingException(c.toString(), "check merging")))
    }
  }
}
