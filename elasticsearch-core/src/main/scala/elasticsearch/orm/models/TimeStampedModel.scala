/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.orm.models

import java.time.OffsetDateTime

trait TimeStampedModel {
  def created: OffsetDateTime

  def modified: OffsetDateTime

}
//
////We need to align the QDBDocument macro with the injected methods
//
//trait TimeStampedModelMeta[BaseDocument] extends HookableObject[BaseDocument] {
//
//  preSaveJsonHooks ++= List(addTimeStamp _)
//  preUpdateJsonHooks ++= List(addTimeStampUpdate _)
//
//  def addTimeStamp(nosqlContext: ESNoSqlContext[_,_], json: JsonObject): JsonObject = {
//    var newJson = json.add("modified", OffsetDateTime.now().asJson)
//    //    var newJson = json ++ Json.obj("modified" -> new OffsetDateTime(2014, 10, 10, 1, 1, 1, 0).toString)
//    if (!newJson.contains("created")) {
//      newJson = json.add("created", OffsetDateTime.now().asJson)
//    }
//    newJson
//  }
//
//  def addTimeStampUpdate(nosqlContext: ESNoSqlContext[_,_], json: JsonObject): JsonObject = {
//    json.add("modified", OffsetDateTime.now().asJson)
//  }
//}
//
//trait UserTrackingModelMeta[BaseDocument] extends TimeStampedModelMeta[BaseDocument] {
//
//  preSaveJsonHooks ++= List(addUserCreated _)
//  preUpdateJsonHooks ++= List(addUserUpdate _)
//
//  private def addUserCreated(nosqlContext: ESNoSqlContext[_,_], json: JsonObject): JsonObject = {
//    json.add("modifiedBy", manager.user.id.asJson)
//
//  }
//
//  private def addUserUpdate(nosqlContext: ESNoSqlContext[_,_], json: JsonObject): JsonObject = {
//    json.add("modifiedBy", manager.user.id.asJson)
//  }
//
//}
