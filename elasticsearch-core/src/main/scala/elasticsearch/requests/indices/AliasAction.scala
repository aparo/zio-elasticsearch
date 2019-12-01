/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.indices

import enumeratum.EnumEntry.Lowercase
import enumeratum._
import io.circe._
sealed trait AliasActionType extends EnumEntry with Lowercase

object AliasActionType
    extends Enum[AliasActionType]
    with CirceEnum[AliasActionType] {

  case object Remove extends AliasActionType

  case object Add extends AliasActionType

  val values = findValues

}

case class AliasAction(action: AliasActionType, index: String, alias: String)

object AliasAction {

  implicit val decodeAliasAction: Decoder[AliasAction] =
    Decoder.instance { c =>
      c.keys.map(_.toList) match {
        case None => Left(DecodingFailure("Missing action in alias", Nil))
        case Some(actions) =>
          val action = actions.head
          for {
            index <- c.downField(action).downField("index").as[String]
            alias <- c.downField(action).downField("alias").as[String]
          } yield
            AliasAction(AliasActionType.withNameInsensitive(action),
                        index,
                        alias)

      }
    }

  implicit val encodeAliasAction: Encoder[AliasAction] = {
    Encoder.instance { obj =>
      Json.obj(
        obj.action.entryName.toLowerCase -> Json
          .obj("index" -> Json.fromString(obj.index),
               "alias" -> Json.fromString(obj.alias))
      )
    }
  }

}
