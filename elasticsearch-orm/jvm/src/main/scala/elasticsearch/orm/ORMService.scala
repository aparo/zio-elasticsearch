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

package elasticsearch.orm

import elasticsearch.responses.DeleteResponse
import elasticsearch.{ ClusterSupport, ZioResponse }
import io.circe.{ Decoder, Encoder }
import logstage.IzLogger
import zio.auth.AuthContext
import zio.schema.SchemaService
import zio.schema.generic.JsonSchema

trait ORMService extends SchemaService with ClusterSupport {
  val ormService: ORMService.Service[Any]
}

object ORMService {
  trait Service[R] {
    def iLogger: IzLogger

    def create[T <: ElasticSearchDocument[T]](
      document: T,
      bulk: Boolean = false,
      forceCreate: Boolean = true,
      index: Option[String] = None,
      docType: Option[String] = None,
      version: Option[Long] = None,
      refresh: Boolean = false,
      userId: Option[String] = None,
      id: Option[String] = None
    )(
      implicit jsonSchema: JsonSchema[T],
      encoder: Encoder[T],
      decoder: Decoder[T],
      authContext: AuthContext
    ): ZioResponse[T]

    def index[T <: ElasticSearchDocument[T]](
      document: T,
      bulk: Boolean = false,
      forceCreate: Boolean = false,
      index: Option[String] = None,
      docType: Option[String] = None,
      version: Option[Long] = None,
      refresh: Boolean = false,
      userId: Option[String] = None,
      id: Option[String] = None
    )(
      implicit jsonSchema: JsonSchema[T],
      encoder: Encoder[T],
      decoder: Decoder[T],
      authContext: AuthContext
    ): ZioResponse[T]

    def delete[T <: ElasticSearchDocument[T]](
      document: T,
      index: Option[String] = None,
      id: Option[String] = None,
      bulk: Boolean = false
    )(
      implicit jsonSchema: JsonSchema[T],
      encoder: Encoder[T],
      decoder: Decoder[T],
      authContext: AuthContext
    ): ZioResponse[DeleteResponse]

  }

  trait Live extends ORMService {
    self =>

    override val ormService: Service[Any] = new Service[Any] {
      override def iLogger: IzLogger = schemaService.iLogger

      override def create[T <: ElasticSearchDocument[T]](
        document: T,
        bulk: Boolean = false,
        forceCreate: Boolean = true,
        index: Option[String] = None,
        docType: Option[String] = None,
        version: Option[Long] = None,
        refresh: Boolean = false,
        userId: Option[String] = None,
        id: Option[String] = None
      )(
        implicit jsonSchema: JsonSchema[T],
        encoder: Encoder[T],
        decoder: Decoder[T],
        authContext: AuthContext
      ): ZioResponse[T] =
        document.elasticsearchMeta
          .es(self)
          .save(
            document,
            bulk = bulk,
            forceCreate = forceCreate,
            index = index,
            docType = docType,
            version = version,
            refresh = refresh,
            userId = userId,
            id = id
          )

      override def index[T <: ElasticSearchDocument[T]](
        document: T,
        bulk: Boolean = false,
        forceCreate: Boolean = false,
        index: Option[String] = None,
        docType: Option[String] = None,
        version: Option[Long] = None,
        refresh: Boolean = false,
        userId: Option[String] = None,
        id: Option[String] = None
      )(
        implicit jsonSchema: JsonSchema[T],
        encoder: Encoder[T],
        decoder: Decoder[T],
        authContext: AuthContext
      ): ZioResponse[T] =
        document.elasticsearchMeta
          .es(self)
          .save(
            document,
            bulk = bulk,
            forceCreate = forceCreate,
            index = index,
            docType = docType,
            version = version,
            refresh = refresh,
            userId = userId,
            id = id
          )

      override def delete[T <: ElasticSearchDocument[T]](
        document: T,
        index: Option[String] = None,
        id: Option[String] = None,
        bulk: Boolean = false
      )(
        implicit jsonSchema: JsonSchema[T],
        encoder: Encoder[T],
        decoder: Decoder[T],
        authContext: AuthContext
      ): ZioResponse[DeleteResponse] =
        document.elasticsearchMeta.es(self).delete(document, bulk = bulk) // todo propagate index id and

    }
  }
}
