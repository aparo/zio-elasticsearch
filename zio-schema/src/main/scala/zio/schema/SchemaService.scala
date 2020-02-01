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

import logstage.IzLogger
import zio.ZIO
import zio.auth.AuthContext
import zio.exception.FrameworkException
import zio.schema.generic.JsonSchema

trait SchemaService {
  val schemaService: SchemaService.Service[Any]
}

object SchemaService {

  trait Service[R] {
    def iLogger: IzLogger

    /***
     * Register a schema in the schema entries
     * @param schema the schema value
     */
    def registerSchema(schema: Schema)(implicit authContext: AuthContext): ZIO[R, FrameworkException, Unit]

    /** *
     * Register a schema in the schema entries
     *
     * @param schema the json schema value
     */
    def registerSchema(
      schema: JsonSchema[_]
    )(implicit authContext: AuthContext): ZIO[R, Throwable, Schema] = {
      val sc = schema.asSchema
      for {
        _ <- ZIO.effect(iLogger.debug(s"Register schema ${sc.name -> "schema_name"}"))
        _ <- registerSchema(sc)
        _ <- ZIO.foreach(schema.relatedDefinitions) { definition =>
          registerSchema(definition.asSchema)
        }
      } yield sc
    }

    def registerSchemas(
      schemas: Seq[Schema]
    )(implicit authContext: AuthContext): Unit =
      schemas.foreach { schema =>
        iLogger.debug(s"Register Schema: ${schema.id}")
        this.registerSchema(schema)
      }

    /***
     * Returns a schema with the given name
     * @param name the name of the schema
     * @return a option value with the schema
     */
    def getSchema(name: String)(
      implicit authContext: AuthContext
    ): ZIO[R, FrameworkException, Schema]

    /**
     * Returns the list of schema ids
     * @return
     */
    def ids(
      implicit authContext: AuthContext
    ): ZIO[R, FrameworkException, Set[String]]

    /**
     * Returns the list of schemas
     * @return
     */
    def schemas(
      implicit authContext: AuthContext
    ): ZIO[R, FrameworkException, List[Schema]]

  }

}
