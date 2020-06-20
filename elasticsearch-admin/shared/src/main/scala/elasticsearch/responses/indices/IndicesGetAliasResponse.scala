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

package elasticsearch.responses.indices

import elasticsearch.responses.AliasDefinition
import io.circe._
import io.circe.derivation.annotations._
import io.circe.syntax._

import scala.collection.mutable
/*
 * Returns an alias.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
 *
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param indices A comma-separated list of index names to filter aliases
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param name A comma-separated list of alias names to return
 */
@JsonCodec
case class IndicesGetAliasResponse(aliases: Map[String, Map[String, AliasDefinition]] = Map.empty) {}

object IndicesGetAliasResponse {

  implicit val decodeIndicesGetAliasesResponse: Decoder[IndicesGetAliasResponse] =
    Decoder.instance { c =>
      c.keys.map(_.toList) match {
        case None => Right(IndicesGetAliasResponse())
        case Some(indices) =>
          Right(
            IndicesGetAliasResponse(
              aliases = indices.flatMap { f =>
                c.downField(f).downField("aliases").as[Map[String, AliasDefinition]].toOption.map { agg =>
                  f -> agg
                }
              }.toMap
            )
          )
      }
    }

  implicit val encodeIndicesGetAliasesResponse: Encoder[IndicesGetAliasResponse] = {
    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      obj.aliases.foreach {
        case (key, aliasDef) =>
          fields += (key -> Json.obj("aliases" -> aliasDef.asJson))
      }
      Json.fromFields(fields)
    }
  }

}

/* Example
{
 "logs_20162801" : {
   "aliases" : {
     "2016" : {
       "filter" : {
         "term" : {
           "year" : 2016
         }
       }
     }
   }
 }
}

 */
