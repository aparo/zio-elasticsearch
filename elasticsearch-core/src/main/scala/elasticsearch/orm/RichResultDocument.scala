/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.orm

import elasticsearch._
import elasticsearch.annotations._
import elasticsearch.requests.{ IndexRequest, UpdateRequest }
import elasticsearch.responses.{ DeleteResponse, ResultDocument, UpdateResponse }
import io.circe._
import io.circe.syntax._

object RichResultDocument {

  implicit class RichResultDocumentImprovements[T: Encoder: Decoder](
    val doc: ResultDocument[T]
  ) {

    def delete(bulk: Boolean = false, refresh: Boolean = false)(
      implicit nosqlContext: ESNoSqlContext,
      encoder: Encoder[T],
      decoder: Encoder[T]
    ): ZioResponse[DeleteResponse] =
      nosqlContext.elasticsearch.delete(
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
    )(implicit nosqlContext: ESNoSqlContext) = {
      val obj = doc.source
//      if (obj.isInstanceOf[NoSqlObject[T]]) {
//        obj = obj.asInstanceOf[NoSqlObject[T]].meta.updateFields(obj)
//        obj
//          .asInstanceOf[NoSqlObject[T]]
//          .meta
//          .preSaveHooks
//          .foreach(f => obj = f(nosqlContext, obj))
//      }

      /*Saving record */

      val json = obj.asJson.asObject.get
//      if (obj.isInstanceOf[NoSqlObject[T]]) {
//        obj
//          .asInstanceOf[NoSqlObject[T]]
//          .meta
//          .preSaveJsonHooks
//          .foreach(f => json = f(nosqlContext, json))
//      }

      val source = Json.fromJsonObject(json).noSpaces

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

      implicit val client = nosqlContext.elasticsearch

      bulk match {
        case true =>
          client.addToBulk(indexAction)
        case false =>
          val res = for {
            resp <- client.indexDocument(indexAction)
          } yield {
            if (obj.isInstanceOf[WithId]) {
              obj.asInstanceOf[WithId].id = resp.id
            }
            if (obj.isInstanceOf[WithType]) {
              obj.asInstanceOf[WithType].`type` = resp.docType
            }
            if (obj.isInstanceOf[WithIndex]) {
              obj.asInstanceOf[WithIndex].index = resp.index
            }
            if (obj.isInstanceOf[WithVersion]) {
              obj.asInstanceOf[WithVersion].version = resp.version
            }
            if (obj.isInstanceOf[WithHiddenId]) {
              obj.asInstanceOf[WithHiddenId]._id = Some(resp.id)
              obj.asInstanceOf[WithHiddenId]._type = Some(resp.docType)
              obj.asInstanceOf[WithHiddenId]._index = Some(resp.index)
              obj.asInstanceOf[WithHiddenId]._version = Some(resp.version)
            }
          }
          nosqlContext.environment.unsafeRun(res)

      }

      /*Post Saving record */
//      if (obj.isInstanceOf[NoSqlObject[_]]) {
//        obj
//          .asInstanceOf[NoSqlObject[T]]
//          .meta
//          .postSaveHooks
//          .foreach(f => obj = f(nosqlContext, obj))
//      }
      obj
    }

    def update(
      values: JsonObject,
      bulk: Boolean = false,
      refresh: Boolean = false
    )(
      implicit nosqlContext: ESNoSqlContext
    ): ZioResponse[UpdateResponse] = {
      //    preDeleteHooks.map(f => obj=f(obj))
      var updateAction =
        new UpdateRequest(
          doc.index,
          id = doc.id,
          body = JsonObject.empty.add("doc", values.asJson),
          refresh = Some(elasticsearch.Refresh.fromValue(refresh))
        )
      //TODO restore
      //    if (this.source.isInstanceOf[NoSqlObject[_]]) {
      //      this.source.asInstanceOf[NoSqlObject[T]].meta.preUpdateHooks.foreach(f => updateAction = f(nosqlContext, updateAction))
      //    }

//      if (doc.source.isInstanceOf[NoSqlObject[_]]) {
//        doc.source
//          .asInstanceOf[NoSqlObject[T]]
//          .meta
//          .preUpdateActionHooks
//          .foreach(f => updateAction = f(nosqlContext, updateAction))
//      }

      implicit val client = nosqlContext.elasticsearch

      val result = if (bulk) {
        client.addToBulk(updateAction)
      } else {
        client.update(updateAction)
      }
//      if (doc.source.isInstanceOf[NoSqlObject[_]]) {
//        val postUpdateHooks = doc.source.asInstanceOf[NoSqlObject[T]].meta.postUpdateHooks
//        if (postUpdateHooks.nonEmpty) {
//          val res = client.awaitResultBulk(result)
//          doc.source
//            .asInstanceOf[NoSqlObject[T]]
//            .meta
//            .getById(index = doc.index, doc.docType, doc.id)
//            .map {
//              case None =>
//              case Some(x) =>
//                var y = x
//                postUpdateHooks.foreach(f => y = f(nosqlContext, y))
//            }
//          result = Future {
//            res
//          }
//        }
//      }
      result
    }

  }

}
