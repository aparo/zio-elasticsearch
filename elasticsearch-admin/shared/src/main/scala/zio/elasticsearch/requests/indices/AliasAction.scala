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

package zio.elasticsearch.requests.indices

import zio.json.ast._
import zio.json._
@jsonEnumLowerCase
sealed trait AliasActionType extends EnumLowerCase {
  def entryName: String
}

object AliasActionType {

  case object Remove extends AliasActionType {
    override def entryName: String = "remove"
  }

  case object Add extends AliasActionType {
    override def entryName: String = "add"
  }

  implicit final val decoder: JsonDecoder[AliasActionType] =
    DeriveJsonDecoderEnum.gen[AliasActionType]
  implicit final val encoder: JsonEncoder[AliasActionType] =
    DeriveJsonEncoderEnum.gen[AliasActionType]
  implicit final val codec: JsonCodec[AliasActionType] = JsonCodec(encoder, decoder)

  def withNameInsensitiveOption(str: String): Option[AliasActionType] =
    str.toLowerCase.fromJson[AliasActionType].toOption

  def withNameInsensitive(str: String): AliasActionType =
    str.toLowerCase.fromJson[AliasActionType].toOption.get

}

case class AliasAction(action: AliasActionType, index: String, alias: String)

object AliasAction {

  implicit val decodeAliasAction: JsonDecoder[AliasAction] = Json.Obj.decoder.mapOrFail { c =>
    c.keys.toList match {
      case Nil => Left("Missing action in alias")
      case actions =>
        val action = actions.head
        for {
          index <- c
            .getOption[Json.Obj](action)
            .getOrElse(Json.Obj())
            .getOption[Json]("index")
            .getOrElse(Json.Null)
            .as[String]
          alias <- c
            .getOption[Json.Obj](action)
            .getOrElse(Json.Obj())
            .getOption[Json]("alias")
            .getOrElse(Json.Null)
            .as[String]
        } yield AliasAction(AliasActionType.withNameInsensitive(action), index, alias)

    }
  }

  implicit val encodeAliasAction: JsonEncoder[AliasAction] = Json.Obj.encoder.contramap { obj =>
    Json.Obj(
      obj.action.entryName.toLowerCase -> Json.Obj("index" -> Json.Str(obj.index), "alias" -> Json.Str(obj.alias))
    )

  }

}
