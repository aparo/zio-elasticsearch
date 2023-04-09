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

package zio.schema.elasticsearch

import zio._
import zio.auth.AuthContext
import zio.exception.FrameworkException
import zio.schema.Schema

trait SchemaService {

  /**
   * * Register a schema in the schema entries
   *
   * @param schema
   *   the json schema value
   */
  def registerSchema(
    schema: ElasticSearchSchema
  )(implicit authContext: AuthContext): ZIO[Any, FrameworkException, Unit]

  /**
   * * Returns a schema with the given name
   * @param name
   *   the name of the schema
   * @return
   *   a option value with the schema
   */
  def getSchema(name: String)(
    implicit
    authContext: AuthContext
  ): ZIO[Any, FrameworkException, ElasticSearchSchema]

  /**
   * Returns the list of schema ids
   * @return
   */
  def ids(
    implicit
    authContext: AuthContext
  ): ZIO[Any, FrameworkException, Set[String]]

  /**
   * Returns the list of schemas
   * @return
   */
  def schemas(
    implicit
    authContext: AuthContext
  ): ZIO[Any, FrameworkException, List[ElasticSearchSchema]]

}

object SchemaService {

  def inMemory: ZLayer[Any, Nothing, SchemaService] =
    ZLayer.succeed(InMemorySchemaService())

  /**
   * * Register a schema in the schema entries
   * @param schema
   *   the schema value
   */
  def registerSchema(
    schema: ElasticSearchSchema
  )(implicit authContext: AuthContext): ZIO[SchemaService, FrameworkException, Unit] =
    ZIO.environmentWithZIO[SchemaService](_.get.registerSchema(schema)).mapError(FrameworkException(_)).unit

  def registerSchemas(
    schemas: Seq[ElasticSearchSchema]
  )(implicit authContext: AuthContext): ZIO[SchemaService, FrameworkException, Unit] =
    ZIO
      .foreach(schemas) { schema =>
//        ZIO.logDebug(s"Register Schema: ${schema.id}") *>
        this.registerSchema(schema)
      }
      .ignore

}
