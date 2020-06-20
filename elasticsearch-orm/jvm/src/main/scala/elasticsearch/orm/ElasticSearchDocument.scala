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

package elasticsearch.orm
import elasticsearch._
import io.circe.{ Decoder, Encoder }
import zio.schema._

trait ElasticSearchDocument[Document] extends SchemaDocument[Document] {
  //  self: Document =>
  def _es: ElasticSearchMeta[Document]

}

trait ElasticSearchMeta[Document] extends SchemaMeta[Document] { self =>

  def es(elasticsearch: ClusterService.Service)(
    implicit encoder: Encoder[Document],
    decoder: Decoder[Document]
  ): ESHelper[Document] =
    new ESHelper[Document](
      schema,
      _schema,
      metaUser = metaUser,
      parentMeta = parentMeta,
      preSaveHooks = self match {
        case value: PreSaveHooks[Document] => value.preSaveHooks
        case _                             => Nil
      },
      preSaveJsonHooks = self match {
        case value: PreSaveJsonHooks => value.preSaveJsonHooks
        case _                       => Nil
      },
      postSaveHooks = self match {
        case value: PostSaveHooks[Document] => value.postSaveHooks
        case _                              => Nil
      },
      preDeleteHooks = self match {
        case value: PreDeleteHooks[Document] => value.preDeleteHooks
        case _                               => Nil
      },
      postDeleteHooks = self match {
        case value: PreSaveHooks[Document] => value.preSaveHooks
        case _                             => Nil
      },
      preUpdateHooks = self match {
        case value: PreUpdateHooks[Document] => value.preUpdateHooks
        case _                               => Nil
      },
      preUpdateJsonHooks = self match {
        case value: PreUpdateJsonHooks[Document] => value.preUpdateJsonHooks
        case _                                   => Nil
      },
      postUpdateHooks = self match {
        case value: PostUpdateHooks[Document] => value.postUpdateHooks
        case _                                => Nil
      }
    )(encoder, decoder, elasticsearch)

}
