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

package zio.elasticsearch.mappings

import java.time.OffsetDateTime

import zio.Chunk
import zio.json._
import zio.json.ast.{Json, _}

final case class MetaSearch(
  var return_fields: Option[Chunk[String]] = None,
  var active: Boolean = true,
  var image: Option[String] = None,
  var highlight_fields: Option[List[Json]] = None,
  search_fields: Option[List[Json]] = None,
  var facet_fields: Option[List[Json]] = None,
  var aggregations: Option[List[Json]] = None
)
object MetaSearch {
  implicit val jsonDecoder: JsonDecoder[MetaSearch] = DeriveJsonDecoder.gen[MetaSearch]
  implicit val jsonEncoder: JsonEncoder[MetaSearch] = DeriveJsonEncoder.gen[MetaSearch]
}

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
final case class MetaUser(
  var track_created: Boolean = false,
  var track_changes: Boolean = false,
  var track_deleted: Boolean = false,
  var auto_owner: Boolean = false,
  var linkedToUser: Boolean = false,
  var field: Option[String] = None
) {
  val ownerSeparator = "---"
  def processPreSaveMetaUser(json: Json.Obj, userId: String): Json.Obj = {
    var newJson = json
    if (track_changes) {
      newJson = newJson.add("user_changed", Json.Obj("user_id" -> userId.asJson, "date" -> OffsetDateTime.now().asJson))
    }
    if (track_created && !newJson.contains("user_created")) {
      newJson = newJson.add("user_created", Json.Obj("user_id" -> userId.asJson, "date" -> OffsetDateTime.now().asJson))
    }
    newJson
  }
  def processAutoOwnerId(id: String, userId: String): String = {
    if (id == userId) return id
    if (id.endsWith(ownerSeparator + userId)) {
      id
    } else {
      id + ownerSeparator + userId
    }
  }
}
object MetaUser {
  implicit val jsonDecoder: JsonDecoder[MetaUser] = DeriveJsonDecoder.gen[MetaUser]
  implicit val jsonEncoder: JsonEncoder[MetaUser] = DeriveJsonEncoder.gen[MetaUser]
}

final case class MetaAliasContext(var filters: List[Json] = Nil, var scripts: Chunk[String] = Chunk.empty)
object MetaAliasContext {
  implicit val jsonDecoder: JsonDecoder[MetaAliasContext] = DeriveJsonDecoder.gen[MetaAliasContext]
  implicit val jsonEncoder: JsonEncoder[MetaAliasContext] = DeriveJsonEncoder.gen[MetaAliasContext]
}

final case class MetaAlias(name: String, var context: MetaAliasContext = MetaAliasContext())
object MetaAlias {
  implicit val jsonDecoder: JsonDecoder[MetaAlias] = DeriveJsonDecoder.gen[MetaAlias]
  implicit val jsonEncoder: JsonEncoder[MetaAlias] = DeriveJsonEncoder.gen[MetaAlias]
}

final case class MetaObject(
  display: Option[Chunk[String]] = None,
  var label: Option[String] = None,
  unique: Option[List[Json]] = None,
  url: Option[String] = None,
  verbose_name_plural: Option[String] = None,
  verbose_name: Option[String] = None,
  alias: List[MetaAlias] = Nil,
  image: Option[String] = None,
  search: Option[MetaSearch] = None,
  user: MetaUser = MetaUser(),
  permissions: Chunk[String] = Chunk.empty
)
object MetaObject {
  implicit val jsonDecoder: JsonDecoder[MetaObject] = DeriveJsonDecoder.gen[MetaObject]
  implicit val jsonEncoder: JsonEncoder[MetaObject] = DeriveJsonEncoder.gen[MetaObject]
}
