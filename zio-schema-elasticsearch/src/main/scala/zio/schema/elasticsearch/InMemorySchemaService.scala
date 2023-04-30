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

import zio.auth.AuthContext
import zio.exception.FrameworkException
import zio.{ Ref, ZIO }

final case class InMemorySchemaService(_schemas: Ref[Map[String, ElasticSearchSchema[_]]]) extends SchemaService {

  /**
   * * Register a schema in the schema entries
   *
   * @param schema
   *   the schema value
   */
  override def registerSchema[T](
    implicit authContext: AuthContext,
    schema: ElasticSearchSchema[T]
  ): ZIO[Any, FrameworkException, Unit] =
    _schemas.update(_ + (schema.id -> schema))

  /**
   * * Returns a schema with the given name
   *
   * @param name
   *   the name of the schema
   * @return
   *   a option value with the schema
   */
  override def getSchema(
    name: String
  )(implicit authContext: AuthContext): ZIO[Any, FrameworkException, ElasticSearchSchema[_]] =
    for {
      schemaOpt <- _schemas.get.map(_.get(name))
      schema <- ZIO.fromOption(schemaOpt).orElseFail(SchemaNotFoundException(name))
    } yield schema

  /**
   * Returns the list of schema ids
   *
   * @return
   */
  override def ids(implicit authContext: AuthContext): ZIO[Any, FrameworkException, Set[String]] =
    _schemas.get.map(_.keys.toSet)

  /**
   * Returns the list of schemas
   *
   * @return
   */
  override def schemas(implicit authContext: AuthContext): ZIO[Any, FrameworkException, List[ElasticSearchSchema[_]]] =
    _schemas.get.map(_.values.toList)

}
