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

package zio.elasticsearch.search

import zio.elasticsearch.common.DefaultOperator
import java.time.{ OffsetDateTime, ZoneOffset }

import scala.util.Try
import zio.elasticsearch.queries._
import zio.json.ast._
import zio.json._

object SearchHelper {

  private def toBoolean(value: String): Boolean = value.toLowerCase() match {
    case "true"  => true
    case "false" => false
    case "on"    => true
    case "off"   => false
    case "1"     => true
    case "0"     => false
    case _       => false
  }

  private def toInt(value: String, default: Int = 0): Int =
    Try(value.toInt).toOption.getOrElse(default)

  private def toJsNumber(value: String): Json =
    value.fromJson[Json].toOption.getOrElse(Json.Null)

  def getQuery(
    field: String,
    query: String,
    kind: String = "==",
    negated: Boolean = false,
    indices: Seq[String] = Nil,
    docTypes: Seq[String] = Nil
  ): Query =
    kind.toLowerCase match {
      case "startswith" | "prefix" =>
        PrefixQuery(field, query)
      case "istartswith" =>
        RegexTermQuery(field, "^" + query.toLowerCase, ignorecase = true)
      case "endsWith" =>
        RegexTermQuery(field, ".*" + query + "$")
      case "iendswith" =>
        RegexTermQuery(field, ".*" + query.toLowerCase + "$", ignorecase = true)

      case "match" =>
        QueryStringQuery(
          query = query + "*",
          fields = List(field),
          defaultOperator = Some(DefaultOperator.AND)
        )

      case "contains" =>
        RegexTermQuery(field, ".*" + query + ".*")
      case "icontains" =>
        RegexTermQuery(
          field,
          ".*" + query.toLowerCase + ".*",
          ignorecase = true
        )
      case "regex" =>
        RegexTermQuery(field, query)
      case "iregex" =>
        RegexTermQuery(field, query.toLowerCase, ignorecase = true)

      case "year" =>
        val value = toInt(query, 2000)
        val start = OffsetDateTime.of(value, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
        val end =
          OffsetDateTime.of(value + 1, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
        RangeQuery(
          field,
          from = Some(Json.Str(start.toString)),
          to = Some(Json.Str(end.toString)),
          includeLower = true,
          includeUpper = false
        )

      case "true" =>
        TermQuery(field, true)
      case "false" =>
        TermQuery(field, false)

      case "==" =>
        TermQuery(field, query)
      case ">" =>
        RangeQuery.gt(field, toJsNumber(query))
      case ">=" =>
        RangeQuery.gte(field, toJsNumber(query))
      case "<" =>
        RangeQuery.lt(field, toJsNumber(query))
      case "<=" =>
        RangeQuery.lte(field, toJsNumber(query))

      case "isnull" =>
        if (toBoolean(query)) {
          MissingQuery(field)
        } else {
          BoolQuery(mustNot = List(MissingQuery(field)))
        }
      case "exists" =>
        if (!negated) {
          //was  toBoolean(query)
          ExistsQuery(field)
        } else {
          BoolQuery(mustNot = List(ExistsQuery(field)))
        }
    }

}
