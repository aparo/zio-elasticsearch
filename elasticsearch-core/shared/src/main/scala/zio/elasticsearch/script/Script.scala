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

package zio.elasticsearch.script

import zio.elasticsearch.ElasticSearchConstants
import zio.json._
import zio.json.ast._

sealed trait Script {
  def lang: String

  def params: Json.Obj

  def getParam(name: String): Option[Json] = params.fields.find(_._1 == name).map(_._2)
}

object Script {
  implicit final val decoder: JsonDecoder[Script] = Json.Obj.decoder.mapOrFail { c =>
    val fields = c.keys
    if (fields.contains("source")) {
      c.as[InlineScript]
    } else if (fields.contains("stored")) {
      c.as[StoredScript]
    } else {
      Left(s"Unable to decode script $c")
    }
  }
  implicit final val encoder: JsonEncoder[Script] = Json.encoder.contramap {
    case obj: InlineScript => obj.toJsonAST.toOption.get
    case obj: StoredScript => obj.toJsonAST.toOption.get
  }

  //  implicit final val encodeScript: JsonEncoder[Script] = JsonEncoder.instance {
//    case obj: InlineScript => obj.asInstanceOf[InlineScript].asJson
//    case obj: StoredScript => obj.asInstanceOf[StoredScript].asJson
//  }
  implicit final val codec: JsonCodec[Script] = JsonCodec(encoder, decoder)

  def apply(source: String, params: Json.Obj): Script =
    new InlineScript(source = source, params = params)
}

@jsonHint("source")
final case class InlineScript(
  source: String,
  lang: String = ElasticSearchConstants.esDefaultScriptingLanguage,
  params: Json.Obj = Json.Obj()
) extends Script
object InlineScript {
  implicit val jsonDecoder: JsonDecoder[InlineScript] = DeriveJsonDecoder.gen[InlineScript]
  implicit val jsonEncoder: JsonEncoder[InlineScript] = DeriveJsonEncoder.gen[InlineScript]
}

@jsonHint("stored")
final case class StoredScript(
  stored: String,
  lang: String = ElasticSearchConstants.esDefaultScriptingLanguage,
  params: Json.Obj = Json.Obj()
) extends Script
object StoredScript {
  implicit val jsonDecoder: JsonDecoder[StoredScript] = DeriveJsonDecoder.gen[StoredScript]
  implicit val jsonEncoder: JsonEncoder[StoredScript] = DeriveJsonEncoder.gen[StoredScript]
}
