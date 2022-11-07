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

import java.time.OffsetDateTime

import zio.json.ast.Json
import zio.json._
import io.circe.derivation.annotations._
import zio.json._

@jsonDerive
final case class MetaSearch(
  var return_fields: Option[List[String]] = None,
  var active: Boolean = true,
  var image: Option[String] = None,
  var highlight_fields: Option[List[Json]] = None,
  search_fields: Option[List[Json]] = None,
  var facet_fields: Option[List[Json]] = None,
  var aggregations: Option[List[Json]] = None
)

/**
 * @param track_created
 *   track if the user creates the object
 * @param track_changes
 *   track if the user changes the object
 * @param track_deleted
 *   track if the user deletes the object
 * @param auto_owner
 *   on saving it mapped only to owner_id
 * @param field
 *   field used for filter user related documents
 */
@jsonDerive
final case class MetaUser(
  var track_created: Boolean = false,
  var track_changes: Boolean = false,
  var track_deleted: Boolean = false,
  var auto_owner: Boolean = false,
  var linkedToUser: Boolean = false,
  var field: Option[String] = None
) {

  val ownerSeparator = "---"

//  def mappings: Map[String, Mapping] = {
//    var result = Map.empty[String, Mapping]
//    if (track_created)
//      result += getMappingAction("user_created")
//    if (track_changes)
//      result += getMappingAction("user_changed")
//    if (track_deleted)
//      result += getMappingAction("user_deleted")
//    if (field.isDefined)
//      result += KeywordMapping.code(field.get)
//    result
//  }
//
//  private def getMappingAction(name: String): (String, ObjectMapping) = {
//    name -> ObjectMapping(
//      properties = Map("user_id" -> KeywordMapping(),
//        "date" -> DateTimeMapping())
//    )
//  }

  def processPreSaveMetaUser(json: Json.Obj, userId: String): Json.Obj = {
    var newJson = json
    if (track_changes) {
      newJson = newJson.add(
        "user_changed",
        Json.obj(
          "user_id" -> userId.asJson,
          "date" -> OffsetDateTime.now().asJson
        )
      )
    }
    //if the
    if (track_created && !newJson.contains("user_created")) {
      newJson = newJson.add(
        "user_created",
        Json.obj(
          "user_id" -> userId.asJson,
          "date" -> OffsetDateTime.now().asJson
        )
      )
    }

    newJson
  }

  def processAutoOwnerId(id: String, userId: String): String = {
    if (id == userId)
      return id

    if (id.endsWith(ownerSeparator + userId)) {
      id
    } else {
      id + ownerSeparator + userId
    }
  }
}

@jsonDerive
final case class MetaAliasContext(
  var filters: List[Json] = Nil,
  var scripts: List[String] = Nil
) {
//  def getFilters: List[Query] = filters.map(_.as[Query].right.get)
}

@jsonDerive
final case class MetaAlias(
  name: String,
  var context: MetaAliasContext = MetaAliasContext()
)

@jsonDerive
final case class MetaObject(
  display: Option[List[String]] = None,
  var label: Option[String] = None,
  unique: Option[List[Json]] = None,
  url: Option[String] = None,
  verbose_name_plural: Option[String] = None,
  verbose_name: Option[String] = None,
  alias: List[MetaAlias] = Nil,
  image: Option[String] = None,
  search: Option[MetaSearch] = None,
  user: MetaUser = MetaUser(),
  permissions: List[String] = Nil
) {

//  def mappings: Map[String, Mapping] = user.mappings
}
