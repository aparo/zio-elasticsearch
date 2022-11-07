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

package zio.elasticsearch.requests.indices

import enumeratum.EnumEntry.Lowercase
import enumeratum._
import zio.json.ast.Json
import zio.json._
sealed trait AliasActionType extends EnumEntry with Lowercase

object AliasActionType extends Enum[AliasActionType] with CirceEnum[AliasActionType] {

  case object Remove extends AliasActionType

  case object Add extends AliasActionType

  val values = findValues

}

case class AliasAction(action: AliasActionType, index: String, alias: String)

object AliasAction {

  implicit val decodeAliasAction: JsonDecoder[AliasAction] =
    JsonDecoder.instance { c =>
      c.keys.map(_.toList) match {
        case None => Left(DecodingFailure("Missing action in alias", Nil))
        case Some(actions) =>
          val action = actions.head
          for {
            index <- c.downField(action).downField("index").as[String]
            alias <- c.downField(action).downField("alias").as[String]
          } yield AliasAction(AliasActionType.withNameInsensitive(action), index, alias)

      }
    }

  implicit val encodeAliasAction: JsonEncoder[AliasAction] = {
    JsonEncoder.instance { obj =>
      Json.obj(
        obj.action.entryName.toLowerCase -> Json
          .obj("index" -> Json.Str(obj.index), "alias" -> Json.Str(obj.alias))
      )
    }
  }

}
