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

package elasticsearch.search

import elasticsearch.queries.{ BoolQuery, MatchAllQuery, Query }

object QueryUtils {

  /**
   * CleanUp a query list
   *
   * @param queries
   *   a list of Query objects
   * @return
   *   a cleaned list of Query objects
   */
  def cleanQueries(queries: List[Query]): List[Query] =
    if (queries.nonEmpty) {
      queries.flatMap {
        case b: BoolQuery =>
          if (b.isEmpty) None else Some(b)
        case q =>
          Some(q)
      }
    } else
      Nil

  /**
   * Given a list of query and filters it generate an optimized query
   * @param queries
   *   a list of queries
   * @param filters
   *   a list of filters
   * @return
   *   a query
   */
  def generateOptimizedQuery(
    queries: List[Query],
    filters: List[Query]
  ): Query = {
    //we remove empty queries if there are
    val validQueries = QueryUtils.cleanQueries(queries)

    //we remove empty filters if there are
    val validFilters = QueryUtils.cleanQueries(filters)

    validFilters.length match {
      case 0 =>
        validQueries.length match {
          case 0 => MatchAllQuery()
          case 1 => validQueries.head
          case _ => BoolQuery(must = validQueries)
        }
      case _ =>
        BoolQuery(must = validQueries, filter = validFilters)
    }
  }

}
