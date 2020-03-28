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

import elasticsearch.analyzers.Analyzer
import elasticsearch.mappings._
import elasticsearch.orm.ORMService
import zio._
import zio.auth.AuthContext
import zio.exception._
import zio.schema._
import zio.schema.generic.JsonSchema

object ElasticSearchSchemaManagerService {
  type ElasticSearchSchemaManagerService = Has[Service]
  trait Service {
    def registerSchema[T](implicit jsonSchema: JsonSchema[T]): ZIO[Any, FrameworkException, Unit]
    def getMapping(schema: Schema): ZIO[Any, FrameworkException, RootDocumentMapping]
    def createMapping[T](implicit jsonSchema: JsonSchema[T]): ZIO[Any, FrameworkException, Unit]
    def createIndicesFromRegisteredSchema(): ZIO[Any, FrameworkException, Unit]
  }

  val live = new ElasticSearchSchemaManagerService
}
