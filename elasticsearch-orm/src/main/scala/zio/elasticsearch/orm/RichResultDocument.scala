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

package zio.elasticsearch.orm

import zio.auth.AuthContext
import zio.json.ast.Json
import zio.ZIO
import zio.elasticsearch.common.{ OpType, Refresh, ResultDocument }
import zio.elasticsearch.common.delete.DeleteResponse
import zio.elasticsearch.common.index.IndexRequest
import zio.elasticsearch.common.update.{ UpdateRequest, UpdateResponse }
import zio.exception.FrameworkException
import zio.schema.elasticsearch.annotations.{ WithHiddenId, WithId, WithIndex, WithType, WithVersion }

object RichResultDocument {

  implicit class RichResultDocumentImprovements(
    val doc: ResultDocument
  ) {

    def delete(bulk: Boolean = false, refresh: Boolean = false)(
      implicit
      ormManager: OrmManager
    ): ZIO[Any, FrameworkException, DeleteResponse] =
      ormManager.elasticSearchService.delete(
        doc.index,
        doc.id,
        bulk = bulk,
        refresh = Some(Refresh.fromValue(refresh))
      )

    def save(
      bulk: Boolean = false,
      forceCreate: Boolean = false,
      index: Option[String] = None,
      docType: Option[String] = None,
      refresh: Boolean = false
    )(implicit ormManager: OrmManager, authContext: AuthContext): ZIO[Any, FrameworkException, Json.Obj] = {
      val client = ormManager.elasticSearchService
      /*Saving record */

      val json = doc.source.getOrElse(Json.Obj())

      var indexAction =
        IndexRequest(
          doc.index,
          id = Some(doc.id),
          body = json,
          refresh = Some(Refresh.fromValue(refresh))
        )

      if (doc.version.getOrElse(-1) != -1)
        indexAction = indexAction.copy(version = Some(doc.version.get))
      if (forceCreate)
        indexAction = indexAction.copy(opType = OpType.create)

      val res = bulk match {
        case true =>
          client.addToBulk(indexAction)
        case false =>
          for {
            resp <- client.index(indexAction)
          } yield ()
//          {
//            if (obj.isInstanceOf[WithId]) {
//              obj.asInstanceOf[WithId].id = resp.id
//            }
//            if (obj.isInstanceOf[WithType]) {
//              obj.asInstanceOf[WithType].`type` = resp.docType
//            }
//            if (obj.isInstanceOf[WithIndex]) {
//              obj.asInstanceOf[WithIndex].index = resp.index
//            }
//            if (obj.isInstanceOf[WithVersion]) {
//              obj.asInstanceOf[WithVersion].version = resp.version
//            }
//            if (obj.isInstanceOf[WithHiddenId]) {
//              obj.asInstanceOf[WithHiddenId]._id = Some(resp.id)
//              obj.asInstanceOf[WithHiddenId]._type = Some(resp.docType)
//              obj.asInstanceOf[WithHiddenId]._index = Some(resp.index)
//              obj.asInstanceOf[WithHiddenId]._version = Some(resp.version)
//            }
//          }
      }

      res *> ZIO.succeed(json)
    }

    def update(
      values: Json.Obj,
      bulk: Boolean = false,
      refresh: Boolean = false
    )(
      implicit
      ormManager: OrmManager,
      authContext: AuthContext
    ): ZIO[Any, FrameworkException, UpdateResponse] = {
      val client = ormManager.elasticSearchService
      val updateAction =
        UpdateRequest(
          doc.index,
          id = doc.id,
          body = Json.Obj().add("doc", values.asJson),
          refresh = Some(zio.elasticsearch.common.Refresh.fromValue(refresh))
        )

      val result = if (bulk) {
        client.addToBulk(updateAction)
      } else {
        client.update(updateAction)
      }
      result
    }

  }

}
