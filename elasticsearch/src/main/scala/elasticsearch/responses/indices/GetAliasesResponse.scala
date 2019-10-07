/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import elasticsearch.queries.Query
import io.circe._
import io.circe.syntax._
import io.circe.derivation.annotations._

import scala.collection.mutable

@JsonCodec
final case class AliasDefinition(filter: Option[Query] = None)

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
 *
 * @param indices A comma-separated list of index names to filter aliases
 * @param name A comma-separated list of alias names to return
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param local Return local information, do not retrieve the state from master node (default: false)
 */
case class GetAliasesResponse(aliases: Map[String, Map[String, AliasDefinition]] = Map.empty) {}

object GetAliasesResponse {

  implicit val decodeGetAliasesResponse: Decoder[GetAliasesResponse] =
    Decoder.instance { c =>
      c.keys.map(_.toList) match {
        case None => Right(GetAliasesResponse())
        case Some(indices) =>
          Right(
            GetAliasesResponse(
              aliases = indices.flatMap { f =>
                c.downField(f).downField("aliases").as[Map[String, AliasDefinition]].toOption.map { agg =>
                  f -> agg
                }
              }.toMap
            )
          )
      }
    }

  implicit val encodeGetAliasesResponse: Encoder[GetAliasesResponse] = {
    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      obj.aliases.foreach {
        case (key, aliasDef) =>
          fields += (key -> Json.obj("aliases" -> aliasDef.asJson))
      }
      Json.obj(fields: _*)
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
