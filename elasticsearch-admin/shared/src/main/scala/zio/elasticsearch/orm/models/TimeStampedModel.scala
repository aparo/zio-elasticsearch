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

package zio.elasticsearch.orm.models

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
//  def addTimeStamp(authContext: AuthContext[_,_], json: Json.Obj): Json.Obj = {
//    var newJson = json.add("modified", OffsetDateTime.now().asJson)
//    //    var newJson = json ++ Json.Obj("modified" -> new OffsetDateTime(2014, 10, 10, 1, 1, 1, 0).toString)
//    if (!newJson.contains("created")) {
//      newJson = json.add("created", OffsetDateTime.now().asJson)
//    }
//    newJson
//  }
//
//  def addTimeStampUpdate(authContext: AuthContext[_,_], json: Json.Obj): Json.Obj = {
//    json.add("modified", OffsetDateTime.now().asJson)
//  }
//}
//
//trait UserTrackingModelMeta[BaseDocument] extends TimeStampedModelMeta[BaseDocument] {
//
//  preSaveJsonHooks ++= List(addUserCreated _)
//  preUpdateJsonHooks ++= List(addUserUpdate _)
//
//  private def addUserCreated(authContext: AuthContext[_,_], json: Json.Obj): Json.Obj = {
//    json.add("modifiedBy", manager.user.id.asJson)
//
//  }
//
//  private def addUserUpdate(authContext: AuthContext[_,_], json: Json.Obj): Json.Obj = {
//    json.add("modifiedBy", manager.user.id.asJson)
//  }
//
//}
