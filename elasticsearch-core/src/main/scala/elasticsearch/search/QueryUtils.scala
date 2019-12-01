/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.search

import elasticsearch.queries.{ BoolQuery, MatchAllQuery, Query }

object QueryUtils {

  /**
   * CleanUp a query list
   *
   * @param queries a list of Query objects
   * @return a cleaned list of Query objects
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
   * @param queries a list of queries
   * @param filters a list of filters
   * @return a query
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
