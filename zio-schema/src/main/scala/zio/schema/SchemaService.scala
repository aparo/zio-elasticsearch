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

import zio.{ Has, ZIO, ZLayer }
import zio.auth.AuthContext
import zio.exception.FrameworkException
import zio.logging.Logging.Logging
import zio.logging._
import zio.schema.generic.JsonSchema

object SchemaService {
  type SchemaService = Has[Service]
  trait Service {

    def loggingService:Logging.Service
    def logger=loggingService.logger

    /***
     * Register a schema in the schema entries
     * @param schema the schema value
     */
    def registerSchema(schema: Schema)(implicit authContext: AuthContext): ZIO[Any, FrameworkException, Unit]

    /** *
     * Register a schema in the schema entries
     *
     * @param schema the json schema value
     */
    def registerSchema(
                        schema: JsonSchema[_]
                      )(implicit authContext: AuthContext): ZIO[Any, Throwable, Schema] = {
      val sc = schema.asSchema
      for {
        _ <- debug(s"Register schema ${sc.name -> "schema_name"}")
        _ <- registerSchema(sc)
        _ <- ZIO.foreach(schema.relatedDefinitions) { definition =>
          registerSchema(definition.asSchema)
        }
      } yield sc
    }

    /***
     * Returns a schema with the given name
     * @param name the name of the schema
     * @return a option value with the schema
     */
    def getSchema(name: String)(
      implicit authContext: AuthContext
    ): ZIO[Any, FrameworkException, Schema]

    /**
     * Returns the list of schema ids
     * @return
     */
    def ids(
      implicit authContext: AuthContext
    ): ZIO[Any, FrameworkException, Set[String]]

    /**
     * Returns the list of schemas
     * @return
     */
    def schemas(
      implicit authContext: AuthContext
    ): ZIO[Any, FrameworkException, List[Schema]]

  }

  def inMemory: ZLayer[Nothing, Nothing, Has[Service]] = ZLayer.succeed(InMemorySchemaService)

  /***
   * Register a schema in the schema entries
   * @param schema the schema value
   */
  def registerSchema(schema: Schema)(implicit authContext: AuthContext): ZIO[SchemaService, FrameworkException, Unit]=
    ZIO.accessM[SchemaService](_.get.registerSchema(schema))



  def registerSchemas(
                       schemas: Seq[Schema]
                     )(implicit authContext: AuthContext): ZIO[SchemaService with Logging, FrameworkException, Unit] =
    ZIO
      .foreach(schemas) { schema =>
        log.debug(s"Register Schema: ${schema.id}") *>
          this.registerSchema(schema)
      }
      .ignore



}
