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

import zio.exception._
import zio.schema.generic.JsonSchema
import zio.schema.{ SchemaService, _ }
import elasticsearch.IndicesService
import elasticsearch.mappings._
import elasticsearch.responses.indices.IndicesCreateResponse
import zio._
trait ElasticSearchSchemaManagerService {
  def registerSchema[T](implicit jsonSchema: JsonSchema[T]): ZIO[Any, FrameworkException, Unit]
  def getMapping[T](implicit jsonSchema: JsonSchema[T]): ZIO[Any, FrameworkException, RootDocumentMapping]
  def createMapping[T](implicit jsonSchema: JsonSchema[T]): ZIO[Any, FrameworkException, IndicesCreateResponse]
  def deleteMapping[T](implicit jsonSchema: JsonSchema[T]): ZIO[Any, FrameworkException, Unit]
  def createIndicesFromRegisteredSchema(): ZIO[Any, FrameworkException, Unit]
}

object ElasticSearchSchemaManagerService {

  val live: ZLayer[SchemaService with IndicesService, Nothing, ElasticSearchSchemaManagerService] =
    ZLayer {
      for {
        schemaService <- ZIO.service[SchemaService]
        indicesService <- ZIO.service[IndicesService]
      } yield ElasticSearchSchemaManagerServiceLive(schemaService, indicesService)
    }

  val liveInMemory: ZLayer[IndicesService, Nothing, ElasticSearchSchemaManagerService] =
    ZLayer {
      for { indicesService <- ZIO.service[IndicesService] } yield ElasticSearchSchemaManagerServiceLive(
        InMemorySchemaService(),
        indicesService
      )
    }

  def registerSchema[T](
    implicit
    jsonSchema: JsonSchema[T]
  ): ZIO[ElasticSearchSchemaManagerService, FrameworkException, Unit] =
    ZIO.environmentWithZIO[ElasticSearchSchemaManagerService](_.get.registerSchema[T])
  def getMapping[T](
    implicit
    jsonSchema: JsonSchema[T]
  ): ZIO[ElasticSearchSchemaManagerService, FrameworkException, RootDocumentMapping] =
    ZIO.environmentWithZIO[ElasticSearchSchemaManagerService](_.get.getMapping[T])
  def createMapping[T](
    implicit
    jsonSchema: JsonSchema[T]
  ): ZIO[ElasticSearchSchemaManagerService, FrameworkException, IndicesCreateResponse] =
    ZIO.environmentWithZIO[ElasticSearchSchemaManagerService](_.get.createMapping[T])

  def deleteMapping[T](
    implicit
    jsonSchema: JsonSchema[T]
  ): ZIO[ElasticSearchSchemaManagerService, FrameworkException, Unit] =
    ZIO.environmentWithZIO[ElasticSearchSchemaManagerService](_.get.deleteMapping[T])

  def createIndicesFromRegisteredSchema(): ZIO[ElasticSearchSchemaManagerService, FrameworkException, Unit] =
    ZIO.environmentWithZIO[ElasticSearchSchemaManagerService](_.get.createIndicesFromRegisteredSchema())

}
