/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.queries

import io.circe._
import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey
import io.circe.syntax._

sealed trait LikeThisObject

case class LikeThisQuery(query: String) extends LikeThisObject

@JsonCodec
case class LikeThisDocId(
  @JsonKey("_index") index: String,
  @JsonKey("_type") `type`: String,
  @JsonKey("_id") id: String
) extends LikeThisObject

@JsonCodec
case class LikeThisDocument(
  @JsonKey("_index") index: String,
  @JsonKey("_type") `type`: String,
  doc: Json
) extends LikeThisObject

object LikeThisObject {
  implicit final val decodeLikeThisObject: Decoder[LikeThisObject] =
    Decoder.instance { c =>
      c.focus.get match {
        case o: Json if o.isObject =>
          val fields = o.asObject.get.keys.toList
          if (fields.contains("_id"))
            o.as[LikeThisDocId]
          else if (fields.contains("doc"))
            o.as[LikeThisDocument]
          else
            Left(DecodingFailure(s"Invalid value for LikeObject: $o", Nil))
        case o: Json if o.isString =>
          o.as[String].map(s => LikeThisQuery(s))
      }

    }

  implicit final val encodeLikeThisObject: Encoder[LikeThisObject] = {

    Encoder.instance {
      case o: LikeThisQuery    => o.query.asJson
      case o: LikeThisDocId    => o.asJson
      case o: LikeThisDocument => o.asJson

    }
  }
}
