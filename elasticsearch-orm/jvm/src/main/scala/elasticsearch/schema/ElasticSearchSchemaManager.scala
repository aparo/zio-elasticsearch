/*
 * Copyright 2019 Alberto Paro
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

package elasticsearch.schema

import elasticsearch.mappings.{ ObjectMapping, RootDocumentMapping }
import zio.auth.AuthContext
import zio.exception.MergeMappingException
import zio.schema.{ Schema, SchemaManager }

class ElasticSearchSchemaManager(val schemaManager: SchemaManager) {

  import MappingImplicits._

  def getMappings(schema: Schema): (String, RootDocumentMapping) = {
    implicit val authContext = AuthContext.SystemUser
    val esProperties = schema.properties
      .filter(_.name != "_id")
      .flatMap { schemafield =>
        schemafield.toMappings(this)(schemaManager, authContext).toList
      }
      .toMap
    schema.name -> RootDocumentMapping(properties = esProperties)
  }

  def getObjectMappings(schema: Schema): (String, ObjectMapping) = {
    implicit val context = AuthContext.SystemUser
    val esProperties = schema.properties
      .filter(_.name != "_id")
      .flatMap { schemafield =>
        schemafield.toMappings(this)(schemaManager, context).toList
      }
      .toMap
    schema.name -> ObjectMapping(properties = esProperties)
  }

  private def mergedMapping(
    schemas: Seq[Schema]
  ): (Seq[MergeMappingException], RootDocumentMapping) = {
    val documentMappings = schemas.map(s => getMappings(s))
    var resultObject = documentMappings.head._2
    val resultObjectName = documentMappings.head._1

    var totalErrors = Seq.empty[MergeMappingException]

    documentMappings.drop(1).foreach {
      case (newName, newObject) =>
        val (errors, optMapping) =
          resultObject.merge(resultObjectName, newName, newObject)
        totalErrors ++= errors
        optMapping.foreach(v => resultObject = v.asInstanceOf[RootDocumentMapping])
    }
    totalErrors -> resultObject
  }

}
