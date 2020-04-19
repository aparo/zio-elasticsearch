/*
 * Copyright 2019-2020 Alberto Paro
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
      case o: LikeThisQuery => o.query.asJson
      case o: LikeThisDocId => o.asJson
      case o: LikeThisDocument => o.asJson

    }
  }
}
