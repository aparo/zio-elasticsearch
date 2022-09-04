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

package zio.schema
import zio.auth.AuthContext
import zio.exception.{ FrameworkException, SchemaNotFoundException }
import zio._

final case class InMemorySchemaService() extends SchemaService {

  private val _schemas = Ref.make(Map.empty[String, Schema])

  /**
   * * Register a schema in the schema entries
   *
   * @param schema
   *   the schema value
   */
  override def registerSchema(schema: Schema)(implicit authContext: AuthContext): ZIO[Any, FrameworkException, Unit] =
    for {
      schemas <- _schemas
      _ <- schemas.update(_ + (schema.name -> schema))
    } yield ()

  /**
   * * Returns a schema with the given name
   *
   * @param name
   *   the name of the schema
   * @return
   *   a option value with the schema
   */
  override def getSchema(name: String)(implicit authContext: AuthContext): ZIO[Any, FrameworkException, Schema] =
    for {
      schemas <- _schemas
      schemaOpt <- schemas.get.map(_.get(name))
      schema <- ZIO.fromOption(schemaOpt).mapError(_ => SchemaNotFoundException(name).asInstanceOf[FrameworkException])
    } yield schema

  /**
   * Returns the list of schema ids
   *
   * @return
   */
  override def ids(implicit authContext: AuthContext): ZIO[Any, FrameworkException, Set[String]] =
    for {
      schemas <- _schemas
      ids <- schemas.get.map(_.keys)
    } yield ids.toSet

  /**
   * Returns the list of schemas
   *
   * @return
   */
  override def schemas(implicit authContext: AuthContext): ZIO[Any, FrameworkException, List[Schema]] =
    for {
      schemas <- _schemas
      values <- schemas.get.map(_.values.toList)
    } yield values

}
